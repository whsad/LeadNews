package com.heima.article.service.impl;

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.heima.article.service.ApCollectionService;
import com.heima.common.constants.BehaviorConstants;
import com.heima.common.constants.HotArticleConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.article.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.mess.UpdateArticleMess;
import com.heima.model.user.pojos.ApUser;
import com.heima.utils.thread.AppThreadLocalUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@Transactional
public class ApCollectionServiceImpl implements ApCollectionService {

    @Autowired
    private CacheService cacheService;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    /**
     * 文章收藏
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult collectionBehavior(CollectionBehaviorDto dto) {
        //1.校验参数
        if (dto == null || checkParam(dto) || dto.getEntryId() == null || dto.getPublishedTime() == null) {
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.校验用户是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }

        UpdateArticleMess mess = new UpdateArticleMess();
        mess.setArticleId(dto.getEntryId());
        mess.setType(UpdateArticleMess.UpdateArticleType.COLLECTION);

        //3.收藏
        if (dto.getOperation() == 0){
            //校验是否已收藏
            String collectionJson = (String)cacheService.hGet(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(), dto.getEntryId().toString());
            if (StringUtils.isNotBlank(collectionJson)){
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST, "已收藏");
            }
            log.info("文章收藏-保存当前key：{}, {}, {}", dto.getEntryId(), user.getId(), JSON.toJSONString(dto));
            cacheService.hPut(BehaviorConstants.COLLECTION_BEHAVIOR + user.getId(), dto.getEntryId().toString(), JSON.toJSONString(dto));
            mess.setAdd(1);
        }else {
            //取消收藏
            log.info("文章收藏-删除当前key：{}, {}, {}", dto.getEntryId(), user.getId(), JSON.toJSONString(dto));
            cacheService.hDelete(BehaviorConstants.COLLECTION_BEHAVIOR +  user.getId(), dto.getEntryId().toString());
            mess.setAdd(-1);
        }
        //发送消息, 数据聚合
        kafkaTemplate.send(HotArticleConstants.HOT_ARTICLE_SCORE_TOPIC, JSON.toJSONString(mess));

        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    private boolean checkParam(CollectionBehaviorDto dto) {
        if (dto.getOperation() > 1 || dto.getOperation() < 0 || dto.getType() > 1 || dto.getType() < 0) {
            return true;
        }
        return false;
    }
}
