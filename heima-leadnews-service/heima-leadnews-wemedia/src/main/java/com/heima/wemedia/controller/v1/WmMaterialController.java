package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.WmMaterialDto;
import com.heima.wemedia.service.WmMaterialService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/material")
public class WmMaterialController {
    @Autowired
    private WmMaterialService wmMaterialService;

    /**
     * 上传图片
     * @param multipartFile
     * @return
     */
    @PostMapping("/upload_picture")
    public ResponseResult upload_picture(@RequestBody MultipartFile multipartFile){
        return wmMaterialService.upload_picture(multipartFile);
    }

    /**
     * 查看图片
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult search_list(@RequestBody WmMaterialDto dto){
        return wmMaterialService.search_list(dto);
    }

    /**
     * 删除图片
     * @param id
     * @return
     */
    @GetMapping("/del_picture/{id}")
    public ResponseResult del_picture(@PathVariable Integer id){
        return wmMaterialService.deleteById(id);
    }

    /**
     * 图片取消收藏
     * @param id
     * @return
     */
    @GetMapping("/cancel_collect/{id}")
    public ResponseResult cancel_collect(@PathVariable Integer id){
        return wmMaterialService.collectOrCancelCollect(id);
    }

    /**
     * 图片收藏
     * @param id
     * @return
     */
    @GetMapping("/collect/{id}")
    public ResponseResult collect(@PathVariable Integer id){
        return wmMaterialService.collectOrCancelCollect(id);
    }
}
