package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.NewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;

public interface WmNewsService extends IService<WmNews> {
    /**
     * 查询文章
     * @param dto
     * @return
     */
    ResponseResult findAll(WmNewsPageReqDto dto);

    /**
     * 发布修改文章或保存为草稿
     * @param dto
     * @return
     */
    ResponseResult submit(WmNewsDto dto);

    /**
     * 查看详情
     * @param id
     * @return
     */
    ResponseResult searchOne(Integer id);

    /**
     * 文章删除
     * @param id
     * @return
     */
    ResponseResult delNews(Integer id);

    /**
     * 文章上下架
     * @param dto
     * @return
     */
    ResponseResult downOrUp(WmNewsDto dto);

    /**
     * 查询文章列表
     * @param dto
     * @return
     */
    ResponseResult WmUserSearchPage(NewsAuthDto dto);

    /**
     * 查询文章详细
     * @param id
     * @return
     */
    ResponseResult selectOne(Integer id);

    /**
     * 人工审核成功或失败
     * @param dto
     * @return
     */
    ResponseResult authFailOrPass(Short status, NewsAuthDto dto);
}
