package com.heima.model.comment.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class CommentDto {
    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 分页条件 最小时间
     */
    private Date minDate;
}
