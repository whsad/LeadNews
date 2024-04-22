package com.heima.user.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.UserRelationDto;
import com.heima.user.service.ApUserFollowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/user/user_follow/")
public class ApUserFollowController {

    @Autowired
    private ApUserFollowService userFollowService;

    /**
     * 关注与取消关注
     * @param dto
     * @return
     */
    @PostMapping
    public ResponseResult userFollow(@RequestBody UserRelationDto dto){
        return userFollowService.userFollow(dto);
    }
}
