package com.heima.behavior.service;

import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApBehaviorService {
    /**
     * 点赞
     * @param dto
     */
    ResponseResult likeBehavior(LikesBehaviorDto dto);

    /**
     * 阅读
     * @param dto
     * @return
     */
    ResponseResult readBehavior(ReadBehaviorDto dto);

    /**
     * 不喜欢
     * @param dto
     * @return
     */
    ResponseResult unLikeBehavior(UnLikesBehaviorDto dto);
}
