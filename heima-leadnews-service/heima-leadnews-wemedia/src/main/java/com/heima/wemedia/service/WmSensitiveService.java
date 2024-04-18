package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.SensitiveDto;
import com.heima.model.wemedia.pojos.WmSensitive;

public interface WmSensitiveService extends IService<WmSensitive> {
    /**
     * 查询列表
     * @param dto
     * @return
     */
    ResponseResult searchSensitiveList(SensitiveDto dto);

    /**
     * 保存敏感词
     * @param adSensitive
     * @return
     */
    ResponseResult saveSensitive(WmSensitive adSensitive);

    /**
     * 修改敏感词
     * @param adSensitive
     * @return
     */
    ResponseResult updateSensitive(WmSensitive adSensitive);
}
