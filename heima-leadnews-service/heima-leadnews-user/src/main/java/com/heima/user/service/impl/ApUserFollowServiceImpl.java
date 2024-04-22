package com.heima.user.service.impl;

import com.heima.common.constants.BehaviorConstants;
import com.heima.common.redis.CacheService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.user.service.ApUserFollowService;
import com.heima.user.service.ApUserService;
import com.heima.utils.thread.AppThreadLocalUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class ApUserFollowServiceImpl implements ApUserFollowService {

    @Autowired
    private CacheService cacheService;

    /**
     * 关注与取消关注
     * @param dto
     * @return
     */
    @Override
    public ResponseResult userFollow(UserRelationDto dto) {
        //1.校验参数
        if (dto == null || checkParam(dto)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.校验是否登录
        ApUser user = AppThreadLocalUtil.getUser();
        if (user == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.NEED_LOGIN);
        }
        Integer apUserId = user.getId();
        Integer followUserId = dto.getAuthorId();
        //3. 关注
        if (dto.getOperation() == 0){
            //将我添加到对方的粉丝中
            cacheService.zAdd(BehaviorConstants.APUSER_FANS_RELATION + followUserId, apUserId.toString(), System.currentTimeMillis());
            //将对方添加到我的关注中
            cacheService.zAdd(BehaviorConstants.APUSER_FOLLOW_RELATION + apUserId, followUserId.toString(), System.currentTimeMillis());
        }else {
            //取消关注
            cacheService.zRemove(BehaviorConstants.APUSER_FANS_RELATION + followUserId, apUserId.toString());
            cacheService.zRemove(BehaviorConstants.APUSER_FOLLOW_RELATION + apUserId, followUserId.toString());
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    private boolean checkParam(UserRelationDto dto) {
        if (dto.getArticleId() == null || dto.getAuthorId() == null || dto.getOperation() > 1 || dto.getOperation() < 0){
            return true;
        }
        return false;
    }
}
