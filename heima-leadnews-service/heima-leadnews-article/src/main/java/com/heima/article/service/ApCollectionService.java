package com.heima.article.service;

import com.heima.model.article.dtos.CollectionBehaviorDto;
import com.heima.model.common.dtos.ResponseResult;

public interface ApCollectionService {

    /**
     * 文章收藏
     * @param dto
     * @return
     */
    ResponseResult collectionBehavior(CollectionBehaviorDto dto);
}
