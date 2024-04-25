package com.heima.comment.service;

import com.heima.model.comment.dtos.CommentRepayDto;
import com.heima.model.comment.dtos.CommentRepayLikeDto;
import com.heima.model.comment.dtos.CommentRepaySaveDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApCommentRepayService {
    /**
     * 评论回复保存
     * @param dto
     * @return
     */
    ResponseResult saveCommentRepay(CommentRepaySaveDto dto);

    /**
     * 查询评论回复列表
     * @param dto
     * @return
     */
    ResponseResult loadCommentRepay(CommentRepayDto dto);

    /**
     * 评论回复点赞
     * @param dto
     * @return
     */
    ResponseResult CommentRepayLike(CommentRepayLikeDto dto);
}
