package com.heima.user.controller.v1;

import com.heima.common.constants.UserConstants;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.user.dtos.AuthDto;
import com.heima.user.service.ApUserAuditService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
public class ApUserAuditController {

    @Autowired
    private ApUserAuditService apUserAuditService;

    /**
     * 查询列表
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult searchUser(@RequestBody AuthDto dto){
        return apUserAuditService.searchUser(dto);
    }

    /**
     * 审核失败
     * @param dto
     * @return
     */
    @PostMapping("/authFail")
    public ResponseResult authFail(@RequestBody AuthDto dto){
        return apUserAuditService.authFailOrPass(dto, UserConstants.FAIL_AUTH);
    }

    /**
     * 审核成功
     */
    @PostMapping("/authPass")
    public ResponseResult authPass(@RequestBody AuthDto dto){
        return apUserAuditService.authFailOrPass(dto, UserConstants.PASS_AUTH);
    }
}
