package com.heima.search.service;


import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.saerch.dtos.UserSearchDto;

import java.io.IOException;

public interface ArticleSearchService {

    /**
     * Es文章分页查询
     */
    ResponseResult search(UserSearchDto userSearchDto) throws IOException;
}
