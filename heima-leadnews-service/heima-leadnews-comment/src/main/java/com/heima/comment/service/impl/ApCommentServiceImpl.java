package com.heima.comment.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.heima.apis.article.IArticleClient;
import com.heima.apis.user.IUserClient;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.comment.pojos.ApComment;
import com.heima.comment.pojos.ApCommentLike;
import com.heima.comment.pojos.CommentVo;
import com.heima.comment.service.ApCommentService;
import com.heima.common.constants.HotArticleConstants;
import com.heima.model.article.pojos.ApArticleConfig;
import com.heima.model.comment.dtos.CommentDto;
import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentSaveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mess.UpdateArticleMess;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.utils.thread.AppThreadLocalUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
public class ApCommentServiceImpl implements ApCommentService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private IArticleClient articleClient;

    @Autowired
    private IUserClient userClient;

    @Autowired
    private IWemediaClient wemediaClient;

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;
    /**
     * 保存评论
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveComment(CommentSaveDto dto) {
        //1.检查参数
        if (dto == null || StringUtils.isBlank(dto.getContent()) || dto.getArticleId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //校验文章是否可以评论
        if (!checkParam(dto.getArticleId())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "该文章不能评论");
        }
        //判断评论内容
        if (dto.getContent().length() > 140){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "评论内容不能大于140位字符");
        }
        //2.判断是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        //3.安全检查
        //自管理的敏感词过滤
        boolean isSensitive = handleSensitiveScan(dto.getContent());
        if (!isSensitive){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前评论内容包含敏感内容");
        }

        //4.查询用户信息
        ApUser dbUser = userClient.findUserById(user.getId());
        if (dbUser == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前登录用户不存在");
        }
        ApComment apComment = new ApComment();
        apComment.setAuthorId(user.getId());
        apComment.setContent(dto.getContent());
        apComment.setCreatedTime(new Date());
        apComment.setEntryId(dto.getArticleId());
        apComment.setImage(dbUser.getImage());
        apComment.setAuthorName(dbUser.getName());
        apComment.setLikes(0);
        apComment.setReply(0);
        apComment.setType((short) 0);
        apComment.setFlag((short) 0);
        mongoTemplate.save(apComment);

        //发送消息, 进行聚合
        UpdateArticleMess mess = new UpdateArticleMess();
        mess.setArticleId(dto.getArticleId());
        mess.setType(UpdateArticleMess.UpdateArticleType.COMMENT);
        mess.setAdd(1);
        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC, JSON.toJSONString(mess));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 查询评论列表
     * @param dto
     * @return
     */
    @Override
    public ResponseResult loadComment(CommentDto dto) {
        //1.校验参数
        if (dto == null || dto.getArticleId() == null || dto.getMinDate() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.加载数据
        Query query = Query.query(Criteria.where("entryId").is(dto.getArticleId()).and("createdTime").lt(dto.getMinDate()));
        query.with(Sort.by(Sort.Direction.DESC, "createdTime")).limit(10);
        List<ApComment> apCommentList = mongoTemplate.find(query, ApComment.class);

        //3.封装数据返回
        //3.1 用户未登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            return ResponseResult.okResult(apCommentList);
        }
        //3.2 用户已登录
        //需要查询当前评论中哪些被点赞
        List<String> idList = apCommentList.stream().map(x -> x.getId()).collect(Collectors.toList());
        Query query1 = Query.query(Criteria.where("commentId").in(idList).and("authorId").is(user.getId()));
        List<ApCommentLike> apCommentLikes = mongoTemplate.find(query1, ApCommentLike.class);
        if (apCommentLikes == null){
            return ResponseResult.okResult(apCommentList);
        }

        List<CommentVo> resultList = new ArrayList<>();
        apCommentList.forEach(x -> {
            CommentVo commentVo = new CommentVo();
            BeanUtils.copyProperties(x, commentVo);
            for (ApCommentLike apCommentLike : apCommentLikes) {
                if (x.getId().equals(apCommentLike.getCommentId())){
                    commentVo.setOperation((short) 0);
                    break;
                }
            }
            resultList.add(commentVo);
        });

        return ResponseResult.okResult(resultList);
    }

    /**
     * 评论点赞
     * @param dto
     * @return
     */
    @Override
    public ResponseResult like(CommentLikeDto dto) {
        //1.校验参数
        if (dto == null || dto.getOperation() == null || StringUtils.isBlank(dto.getCommentId())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.校验登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);
        //3.点赞
        if (apComment != null && dto.getOperation() == 0){
            //更新评论点赞数量
            apComment.setLikes(apComment.getLikes() + 1);
            mongoTemplate.save(apComment);

            //保存评论点赞数据
            ApCommentLike apCommentLike = new ApCommentLike();
            apCommentLike.setCommentId(apComment.getId());
            apCommentLike.setAuthorId(user.getId());
            mongoTemplate.save(apCommentLike);
        }else {
            //更新评论点赞数据
            int tmp = apComment.getLikes() - 1;
            tmp = tmp < 1 ? 0 : tmp;
            apComment.setLikes(tmp);
            mongoTemplate.save(apComment);

            //删除评论点赞数据
            Query query = Query.query(Criteria.where("commentId").is(apComment.getId()).and("authorId").is(user.getId()));
            mongoTemplate.remove(query, ApCommentLike.class);
        }

        //4.取消点赞
        Map<String, Object> result = new HashMap<>();
        result.put("likes", apComment.getLikes());
        return ResponseResult.okResult(result);
    }

    /**
     * 校验文章是否可以评论
     * @param articleId
     * @return
     */
    private boolean checkParam(Long articleId) {
        //是否可以评论
        ResponseResult configResult = articleClient.findArticleConfigByArticleId(articleId);
        if (!configResult.getCode().equals(200) || configResult.getData() == null){
            return false;
        }

        ApArticleConfig apArticleConfig = JSON.parseObject(JSON.toJSONString(configResult.getData()), ApArticleConfig.class);
        if (apArticleConfig == null || !apArticleConfig.getIsComment()){
            return false;
        }
        return true;
    }

    /**
     * 自管理的敏感词审核
     * @param content
     * @return
     */
    private boolean handleSensitiveScan(String content) {
        boolean flag = true;

        //获取所有的敏感词
        List<WmSensitive> wmSensitives = wemediaClient.selectSensitives();

        List<String> sensitiveList = wmSensitives.stream().map(WmSensitive::getSensitives).collect(Collectors.toList());

        //初始化敏感词库
        SensitiveWordUtil.initMap(sensitiveList);

        //查看文章中是否包含敏感词
        Map<String, Integer> map = SensitiveWordUtil.matchWords(content);
        if (map.size() > 0) {
            flag = false;
        }
        return flag;
    }
}
