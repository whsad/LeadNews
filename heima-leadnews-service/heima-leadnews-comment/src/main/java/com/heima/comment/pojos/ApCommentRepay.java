package com.heima.comment.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Document("ap_comment_repay")
public class ApCommentRepay {
    /**
     * id
     */
    private String id;
    /**
     * 用户id
     */
    private Integer authorId;
    /**
     * 用户名称
     */
    private String authorName;
    /**
     * 评论id
     */
    private String commentId;
    /**
     * 回复内容
     */
    private String content;
    /**
     * 点赞数量
     */
    private Integer likes;
    /**
     * 回复开始时间
     */
    private Date createdTime;
    /**
     * 修改时间
     */
    private Date updatedTime;
}
