package com.heima.model.comment.dtos;

import lombok.Data;

@Data
public class CommentRepaySaveDto {
    /**
     * 评论Id
     */
    private String commentId;
    /**
     * 评论内容
     */
    private String content;
}
