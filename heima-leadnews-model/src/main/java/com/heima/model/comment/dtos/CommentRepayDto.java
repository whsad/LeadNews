package com.heima.model.comment.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class CommentRepayDto {
    /**
     * 评论id
     */
    private String commentId;
    /**
     * 列表中的最小时间
     */
    private Date minDate;
    /**
     * 分页条数
     */
    private Integer size;
}
