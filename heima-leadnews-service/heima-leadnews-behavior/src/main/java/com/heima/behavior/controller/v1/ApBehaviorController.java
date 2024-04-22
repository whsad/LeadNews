package com.heima.behavior.controller.v1;

import com.heima.behavior.service.ApBehaviorService;
import com.heima.model.behavior.dtos.LikesBehaviorDto;
import com.heima.model.behavior.dtos.ReadBehaviorDto;
import com.heima.model.behavior.dtos.UnLikesBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1")
public class ApBehaviorController {

    @Autowired
    private ApBehaviorService behaviorService;

    /**
     * 点赞
     * @param dto
     * @return
     */
    @PostMapping("/likes_behavior")
    public ResponseResult likeBehavior(@RequestBody LikesBehaviorDto dto){
        return behaviorService.likeBehavior(dto);
    }

    /**
     * 阅读
     * @param dto
     * @return
     */
    @PostMapping("/read_behavior")
    public ResponseResult readBehavior(@RequestBody ReadBehaviorDto dto){
        return behaviorService.readBehavior(dto);
    }

    /**
     * 不喜欢
     * @param dto
     * @return
     */
    @PostMapping("/un_likes_behavior")
    public ResponseResult unLikeBehavior(@RequestBody UnLikesBehaviorDto dto){
        return behaviorService.unLikeBehavior(dto);
    }
}
