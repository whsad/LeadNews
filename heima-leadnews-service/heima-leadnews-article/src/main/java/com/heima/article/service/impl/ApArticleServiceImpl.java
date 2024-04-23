package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.article.IArticleClient;
import com.heima.article.mapper.ApArticleConfigMapper;
import com.heima.article.mapper.ApArticleContentMapper;
import com.heima.article.mapper.ApArticleMapper;
import com.heima.article.service.ApArticleService;
import com.heima.article.service.ArticleFreemarkerService;
import com.heima.common.constants.ArticleConstants;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dtos.ArticleDto;
import com.heima.model.article.dtos.ArticleHomeDto;
import com.heima.model.article.dtos.ArticleInfoDto;
import com.heima.model.article.pojos.ApArticle;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.article.pojos.ApArticleContent;
import com.heima.model.article.vos.HotArticleVo;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import io.seata.spring.annotation.GlobalTransactional;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class ApArticleServiceImpl extends ServiceImpl<ApArticleMapper, ApArticle> implements ApArticleService {

    //单页最大加载的数字
    private final static Short  MAX_PAGE_SIZE = 50;

    @Autowired
    private ApArticleMapper articleMapper;
    @Autowired
    private ApArticleConfigMapper apArticleConfigMapper;
    @Autowired
    private ApArticleContentMapper apArticleContentMapper;
    @Autowired
    private ArticleFreemarkerService articleFreemarkerService;
    @Autowired
    private CacheService cacheService;

    /**
     * 根据参数加载文章列表
     *
     * @param loadType 1为加载更多 2为加载最新
     * @param dto
     * @return
     */
    @Override
    public ResponseResult load(Short loadType, ArticleHomeDto dto) {
        //1.校验参数
        Integer size = dto.getSize();
        if (size == null || size == 0){
            size = 10;
        }
        size = Math.min(size, MAX_PAGE_SIZE);
        dto.setSize(size);

        //类型校验
        if (!loadType.equals(ArticleConstants.LOADTYPE_LOAD_MORE) && !loadType.equals(ArticleConstants.LOADTYPE_LOAD_NEW)){
            loadType = ArticleConstants.LOADTYPE_LOAD_MORE;
        }
        //文章频道校验
        if (StringUtils.isEmpty(dto.getTag())){
            dto.setTag(ArticleConstants.DEFAULT_TAG);
        }

        //时间校验
        if (dto.getMaxBehotTime() == null) dto.setMaxBehotTime(new Date());
        if (dto.getMinBehotTime() == null) dto.setMinBehotTime(new Date());

        //查询数据
        List<ApArticle> apArticles = articleMapper.loadArticleList(dto, loadType);

        //结果封装
        return ResponseResult.okResult(apArticles);
    }

    /**
     * 加载文章列表
     *
     * @param dto
     * @param type      1 加载更多 2 加载最新
     * @param firstPage true 是首页 false 非首页
     * @return
     */
    @Override
    public ResponseResult load2(ArticleHomeDto dto, Short type, boolean firstPage) {
        if (firstPage){
            String jsonStr = cacheService.get(ArticleConstants.HOT_ARTICLE_FIRST_PAGE + dto.getTag());
            if (StringUtils.isNotBlank(jsonStr)){
                List<HotArticleVo> hotArticleVoList = JSON.parseArray(jsonStr, HotArticleVo.class);
                return ResponseResult.okResult(hotArticleVoList);
            }
        }
        return load(type, dto);
    }

    /**
     * 保存或修改文章
     * @param dto
     * @return
     */
    @Override
    //@GlobalTransactional
    public ResponseResult saveArticle(ArticleDto dto) {
        if(dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        ApArticle apArticle = new ApArticle();
        BeanUtils.copyProperties(dto, apArticle);

        //判断是否存在文章id
        if (dto.getId() == null){
            //1.保存文章
            save(apArticle);

            //2.保存文章配置
            ApArticleConfig apArticleConfig = new ApArticleConfig(apArticle.getId());
            apArticleConfigMapper.insert(apArticleConfig);

            //3.保存文章内容
            ApArticleContent apArticleContent = new ApArticleContent();
            apArticleContent.setArticleId(apArticle.getId());
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.insert(apArticleContent);

        }else {
            //1.修改文章
            updateById(apArticle);

            //2.保存文章内容
            ApArticleContent apArticleContent = apArticleContentMapper
                    .selectOne(Wrappers.<ApArticleContent>lambdaQuery()
                            .eq(ApArticleContent::getArticleId, dto.getId()));
            apArticleContent.setContent(dto.getContent());
            apArticleContentMapper.updateById(apArticleContent);
        }

        //异步调用 生产静态文件上次到minio中
        articleFreemarkerService.buildArticleToMinIO(apArticle, dto.getContent());

        return ResponseResult.okResult(apArticle.getId());
    }

    /**
     * 数据回显
     * @param dto
     * @return
     */
    @Override
    public ResponseResult ApLoadArticle(ArticleInfoDto dto) {
        //1.校验参数
        if (dto == null || dto.getAuthorId() == null || dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.校验用户是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            return  ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        Boolean isLike = false, isUnlike = false, isCollection = false, isFollow = false;
        //3.喜欢行为
        Object LikeBehavior = cacheService.hGet(BehaviorConstants.LIKE_BEHAVIOR + dto.getArticleId(), user.getId().toString());
        if (LikeBehavior != null){
            isLike = true;
        }
        //4.不喜欢行为
        Object unLikeBehavior = cacheService.hGet(BehaviorConstants.UN_LIKE_BEHAVIOR + dto.getArticleId(), user.getId().toString());
        if (unLikeBehavior != null){
            isUnlike = true;
        }
        //5.是否收藏
        String collectionBehavior = (String) cacheService.hGet(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(), dto.getArticleId().toString());
        if (StringUtils.isNotBlank(collectionBehavior)){
            isCollection = true;
        }
        //6.是否关注
        Double followBehavior = cacheService.zScore(BehaviorConstants.APUSER_FOLLOW_RELATION + user.getId(), dto.getAuthorId().toString());
        if (followBehavior != null){
            isFollow = true;
        }

        //7.回填数据
        Map<String, Boolean> map = new HashMap<>();
        map.put("islike", isLike);
        map.put("isunlike", isUnlike);
        map.put("iscollection", isCollection);
        map.put("isfollow", isFollow);

        return ResponseResult.okResult(map);
    }
}


























