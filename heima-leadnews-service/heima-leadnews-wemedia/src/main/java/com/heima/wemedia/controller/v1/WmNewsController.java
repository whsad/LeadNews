package com.heima.wemedia.controller.v1;

import com.heima.common.constants.WemediaConstants;
import com.heima.common.constants.WmNewsMessageConstants;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.NewsAuthDto;
import com.heima.model.wemedia.dtos.WmNewsDto;
import com.heima.model.wemedia.dtos.WmNewsPageReqDto;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.service.WmNewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/news")
public class WmNewsController {

    @Autowired
    private WmNewsService wmNewsService;

    /**
     * 查询文章
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult findAll(@RequestBody WmNewsPageReqDto dto){
        return wmNewsService.findAll(dto);
    }

    /**
     * 发布修改文章或保存为草稿
     * @param dto
     * @return
     */
    @PostMapping("/submit")
    public ResponseResult submit(@RequestBody WmNewsDto dto){
        return wmNewsService.submit(dto);
    }

    /**
     * 查看详情
     * @param id
     * @return
     */
    @GetMapping("/one/{id}")
    public ResponseResult searchOne(@PathVariable Integer id){
        return wmNewsService.searchOne(id);
    }

    /**
     * 文章删除
     * @param id
     * @return
     */
    @GetMapping("/del_news/{id}")
    public ResponseResult delNews(@PathVariable Integer id){
        return wmNewsService.delNews(id);
    }

    /**
     * 文章上下架
     * @param dto
     * @return
     */
    @PostMapping("/down_or_up")
    public ResponseResult downOrUp(@RequestBody WmNewsDto dto){
        return wmNewsService.downOrUp(dto);
    }

    /**
     * 自媒体文章人工审核
     */
    @PostMapping("/list_vo")
    public ResponseResult WmUserSearchPage(@RequestBody NewsAuthDto dto){
        return wmNewsService.WmUserSearchPage(dto);
    }

    /**
     * 查询文章详细
     */
    @GetMapping("/one_vo/{id}")
    public ResponseResult selectOne(@PathVariable Integer id){
        return wmNewsService.selectOne(id);
    }

    /**
     * 审核失败
     */
    @PostMapping("/auth_fail")
    public ResponseResult authFail(@RequestBody NewsAuthDto dto){
        return wmNewsService.authFailOrPass(WemediaConstants.WM_NEWS_AUTH_FAIL, dto);
    }

    /**
     * 审核成功
     */
    @PostMapping("/auth_pass")
    public ResponseResult authPass(@RequestBody NewsAuthDto dto){
        return wmNewsService.authFailOrPass(WemediaConstants.WM_NEWS_AUTH_PASS, dto);
    }
}
