package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;

public interface WmNewsService extends IService<WmNews> {
    ResponseResult findAll(WmNewsPageReqDto dto);

    ResponseResult submit(WmNewsDto dto);

    /**
     * 查看详情
     * @param id
     * @return
     */
    ResponseResult searchOne(Integer id);
}
