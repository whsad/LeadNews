package com.heima.search.service;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.saerch.dtos.UserSearchDto;

public interface ApAssociateWordsService {

    /**
     * 联想词
     * @param userSearchDto
     * @return
     */
    ResponseResult findAssociate(UserSearchDto userSearchDto);
}
