package com.heima.user.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;

public interface ApUserFollowService {
    /**
     * 关注与取消关注
     * @param dto
     * @return
     */
    ResponseResult userFollow(UserRelationDto dto);
}
