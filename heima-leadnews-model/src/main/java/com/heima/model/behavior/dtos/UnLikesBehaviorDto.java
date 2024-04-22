package com.heima.model.behavior.dtos;

import lombok.Data;

@Data
public class UnLikesBehaviorDto {

    /**
     * 文章id
     */
    private Long articleId;
    /**
     * 0 不喜欢 1 取消不喜欢
     */
    private Short type;
}
