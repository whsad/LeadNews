package com.heima.comment.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("ap_comment_like")
public class ApCommentLike {
    /**
     * id
     */
    private String id;
    /**
     * 用户Id
     */
    private Integer authorId;
    /**
     * 评论Id
     */
    private String commentId;
    /**
     * 0：点赞
     * 1：取消点赞
     */
    private Short operation;
}
