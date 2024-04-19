package com.heima.user.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.AuthDto;
import com.heima.model.user.pojos.ApUserRealname;

public interface ApUserAuditService extends IService<ApUserRealname> {

    /**
     * 查询列表
     * @param dto
     * @return
     */
    ResponseResult searchUser(AuthDto dto);


    /**
     * 审核成功或失败
     * @param dto
     * @return
     */
    ResponseResult authFailOrPass(AuthDto dto, Short status);
}
