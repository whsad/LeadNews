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

    @PostMapping("/upload_picture")
    public ResponseResult upload_picture(@RequestBody MultipartFile multipartFile){
        return wmMaterialService.upload_picture(multipartFile);
    }

    @PostMapping("/list")
    public ResponseResult search_list(WmMaterialDto dto){
        return wmMaterialService.search_list(dto);
    }
}
