package com.heima.model.user.dtos;

import com.heima.model.common.dtos.PageRequestDto;
import com.heima.model.common.dtos.PageResponseResult;
import lombok.Data;

@Data
public class AuthDto extends PageRequestDto {
    /**
     * 主键
     */
    private Integer id;
    /**
     * 审核结果
     */
    private String msg;
    /**
     * 审核状态
     */
    private Integer status;
}
