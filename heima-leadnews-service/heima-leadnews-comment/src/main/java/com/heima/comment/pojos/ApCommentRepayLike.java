package com.heima.comment.pojos;

import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document("ap_comment_repay_like")
public class ApCommentRepayLike {
    /**
     * id
     */
    private String id;
    /**
     * 用户id
     */
    private Integer authorId;
    /**
     * 评论回复的id
     */
    private String commentRepayId;
    /**
     * 0：点赞
     * 1：取消点赞
     */
    private Short operation;
}
