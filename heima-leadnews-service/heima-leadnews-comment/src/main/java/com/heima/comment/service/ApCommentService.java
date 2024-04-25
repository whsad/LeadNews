package com.heima.comment.service;

import com.heima.model.comment.dtos.CommentDto;
import com.heima.model.comment.dtos.CommentLikeDto;
import com.heima.model.comment.dtos.CommentSaveDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApCommentService {
    /**
     * 保存评论
     * @param dto
     * @return
     */
    ResponseResult saveComment(CommentSaveDto dto);

    /**
     * 查询评论列表
     * @param dto
     * @return
     */
    ResponseResult loadComment(CommentDto dto);

    /**
     * 评论点赞
     * @param dto
     * @return
     */
    ResponseResult like(CommentLikeDto dto);
}
