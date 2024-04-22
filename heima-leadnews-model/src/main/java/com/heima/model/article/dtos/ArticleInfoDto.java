package com.heima.model.article.dtos;

import lombok.Data;

@Data
public class ArticleInfoDto {

    /**
     * 文章id
     */
    private Long articleId;

    /**
     * 作者id
     */
    private Integer authorId;
}
