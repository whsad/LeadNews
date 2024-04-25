package com.heima.comment.controller.v1;

import com.heima.comment.service.ApCommentRepayService;
import com.heima.comment.service.ApCommentService;
import com.heima.model.comment.dtos.CommentRepayDto;
import com.heima.model.comment.dtos.CommentRepayLikeDto;
import com.heima.model.comment.dtos.CommentRepaySaveDto;
import com.heima.model.common.dtos.ResponseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/comment_repay")
public class ApCommentRepayController {

    @Autowired
    private ApCommentRepayService commentRepayService;

    /**
     * 评论回复保存
     * @param dto
     * @return
     */
    @PostMapping("/save")
    public ResponseResult saveCommentRepay(@RequestBody CommentRepaySaveDto dto){
        return commentRepayService.saveCommentRepay(dto);
    }

    /**
     * 查询评论回复列表
     * @param dto
     * @return
     */
    @PostMapping("/load")
    public ResponseResult loadCommentRepay(@RequestBody CommentRepayDto dto){
        return commentRepayService.loadCommentRepay(dto);
    }

    @PostMapping("/like")
    public ResponseResult CommentRepayLike(@RequestBody CommentRepayLikeDto dto){
        return commentRepayService.CommentRepayLike(dto);
    }
}
