package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.SensitiveDto;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/sensitive")
public class WmSensitiveController {

    @Autowired
    private WmSensitiveService wmSensitiveService;

    /**
     * 查询列表
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult searchSensitiveList(@RequestBody SensitiveDto dto){
        return wmSensitiveService.searchSensitiveList(dto);
    }

    /**
     * 保存敏感词
     * @param AdSensitive
     * @return
     */
    @PostMapping("/save")
    public ResponseResult saveSensitive(@RequestBody WmSensitive AdSensitive){
        return wmSensitiveService.saveSensitive(AdSensitive);
    }

    /**
     * 删除敏感词
     * @param id
     * @return
     */
    @DeleteMapping("/del/{id}")
    public ResponseResult delSensitive(@PathVariable Integer id){
        return ResponseResult.okResult(wmSensitiveService.removeById(id));
    }

    /**
     * 修改敏感词
     * @param AdSensitive
     * @return
     */
    @PostMapping("/update")
    public ResponseResult updateSensitive(@RequestBody WmSensitive AdSensitive){
        return wmSensitiveService.updateSensitive(AdSensitive);
    }
}
