package com.heima.comment.service.impl;

import com.alibaba.fastjson.JSON;
import com.heima.apis.user.IUserClient;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.comment.pojos.ApComment;
import com.heima.comment.pojos.ApCommentRepay;
import com.heima.comment.pojos.ApCommentRepayLike;
import com.heima.comment.pojos.CommentRepayVo;
import com.heima.comment.service.ApCommentRepayService;
import com.heima.common.constants.HotArticleConstants;
import com.heima.model.comment.dtos.CommentRepayDto;
import com.heima.model.comment.dtos.CommentRepayLikeDto;
import com.heima.model.comment.dtos.CommentRepaySaveDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mess.UpdateArticleMess;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.utils.common.SensitiveWordUtil;
import com.heima.utils.thread.AppThreadLocalUtil;
import org.apache.commons.lang.StringUtils;
import org.apache.ibatis.annotations.Update;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class ApCommentRepayServiceImpl implements ApCommentRepayService {

    @Autowired
    private IWemediaClient wemediaClient;
    @Autowired
    private MongoTemplate mongoTemplate;
    @Autowired
    private IUserClient userClient;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 评论回复保存
     * @param dto
     * @return
     */
    @Override
    public ResponseResult saveCommentRepay(CommentRepaySaveDto dto) {
        //1.校验参数
        if (dto ==null || StringUtils.isBlank(dto.getContent()) || StringUtils.isBlank(dto.getCommentId())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //判断评论内容
        if (dto.getContent().length() > 140){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "评论内容不能大于140位字符");
        }
        //校验回复的评论是否存在
        ApComment apComment = mongoTemplate.findById(dto.getCommentId(), ApComment.class);
        if (apComment == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //2.校验是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        //3.安全检测
        //自管理的敏感词过滤
        boolean isSensitive = handleSensitiveScan(dto.getContent());
        if (!isSensitive){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前评论内容包含敏感内容");
        }

        ApUser dbUser = userClient.findUserById(user.getId());
        if (dbUser == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "当前登录用户不存在");
        }
        ApCommentRepay apCommentRepay = new ApCommentRepay();
        apCommentRepay.setAuthorId(user.getId());
        apCommentRepay.setAuthorName(dbUser.getName());
        apCommentRepay.setCommentId(dto.getCommentId());
        apCommentRepay.setContent(dto.getContent());
        apCommentRepay.setLikes(0);
        apCommentRepay.setCreatedTime(new Date());
        apCommentRepay.setUpdatedTime(new Date());
        mongoTemplate.save(apCommentRepay);

        //更新回复数量
        apComment.setReply(apComment.getReply() + 1);
        mongoTemplate.save(apComment);

        //发送消息, 进行聚合
        UpdateArticleMess mess = new UpdateArticleMess();
        mess.setArticleId(apComment.getEntryId());
        mess.setType(UpdateArticleMess.UpdateArticleType.COMMENT);
        mess.setAdd(1);
        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC, JSON.toJSONString(mess));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 查询评论回复列表
     * @param dto
     * @return
     */
    @Override
    public ResponseResult loadCommentRepay(CommentRepayDto dto) {
        //1.校验参数
        if (dto == null || dto.getMinDate() == null || StringUtils.isBlank(dto.getCommentId())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.加载数据
        Query query = Query.query(Criteria.where("commentId").is(dto.getCommentId()).and("createdTime").lt(dto.getMinDate()));
        query.with(Sort.by(Sort.DEFAULT_DIRECTION, "createdTime")).limit(20);
        List<ApCommentRepay> apCommentRepays = mongoTemplate.find(query, ApCommentRepay.class);

        //3.封装数据返回
        //3.1 用户未登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            return ResponseResult.okResult(apCommentRepays);
        }
        //3.2 用户已登录
        //需要查询当前评论中哪些被点赞
        List<String> idList = apCommentRepays.stream().map(x -> x.getId()).collect(Collectors.toList());
        Query query1 = Query.query(Criteria.where("commentRepayId").in(idList).and("authorId").is(user.getId()));
        List<ApCommentRepayLike> apCommentRepayLikes = mongoTemplate.find(query1, ApCommentRepayLike.class);
        if (apCommentRepayLikes == null || apCommentRepayLikes.size() == 0){
            return ResponseResult.okResult(apCommentRepays);
        }

        List<CommentRepayVo> resultList = new ArrayList<>();
        apCommentRepays.forEach(x -> {
            CommentRepayVo vo = new CommentRepayVo();
            BeanUtils.copyProperties(x, vo);
            for (ApCommentRepayLike apCommentRepayLike : apCommentRepayLikes) {
                if (x.getId().equals(apCommentRepayLike.getCommentRepayId())){
                    vo.setOperation((short) 0);
                    break;
                }
            }
            resultList.add(vo);
        });
        return ResponseResult.okResult(resultList);
    }

    /**
     * 评论回复点赞
     * @param dto
     * @return
     */
    @Override
    public ResponseResult CommentRepayLike(CommentRepayLikeDto dto) {
        //1.校验参数
        if (dto == null || dto.getOperation() == null || StringUtils.isBlank(dto.getCommentRepayId())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.校验登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        ApCommentRepay apCommentRepay = mongoTemplate.findById(dto.getCommentRepayId(), ApCommentRepay.class);

        //3.点赞
        if (apCommentRepay != null && dto.getOperation() == 0){
            //更新评论点赞
            apCommentRepay.setLikes(apCommentRepay.getLikes() + 1);
            mongoTemplate.save(apCommentRepay);

            //保存评论点赞数据
            ApCommentRepayLike apCommentRepayLike = new ApCommentRepayLike();
            apCommentRepayLike.setCommentRepayId(apCommentRepay.getId());
            apCommentRepayLike.setAuthorId(user.getId());
            mongoTemplate.save(apCommentRepayLike);
        }else {
            //更新评论点赞数据
            int tmp = apCommentRepay.getLikes() - 1;
            tmp = tmp < 1 ? 0 : tmp;
            apCommentRepay.setLikes(tmp);
            mongoTemplate.save(apCommentRepay);

            //删除评论点赞数据
            Query query = Query.query(Criteria.where("commentRepayId").is(apCommentRepay.getId()).and("authorId").is(user.getId()));
            mongoTemplate.remove(query, ApCommentRepayLike.class);
        }

        //4.取消点赞
        Map<String, Object> result = new HashMap<>();
        result.put("likes", apCommentRepay.getLikes());
        return ResponseResult.okResult(result);
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
