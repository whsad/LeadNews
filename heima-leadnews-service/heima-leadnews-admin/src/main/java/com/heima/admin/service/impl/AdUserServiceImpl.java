package com.heima.admin.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.admin.mapper.AdUserMapper;
import com.heima.admin.service.AdUserService;
import com.heima.model.admin.dtos.AdUserDto;
import com.heima.model.admin.pojos.AdUser;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.utils.common.AppJwtUtil;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.HashMap;
import java.util.Map;

@Service
public class AdUserServiceImpl extends ServiceImpl<AdUserMapper, AdUser> implements AdUserService {

    /**
     * 平台管理端登录
     * @param dto
     */
    @Override
    public ResponseResult login(AdUserDto dto) {
        //1.检查参数
        if (StringUtils.isBlank(dto.getName()) || StringUtils.isBlank(dto.getPassword())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.查询用户
        AdUser aduser = getOne(Wrappers.<AdUser>lambdaQuery().eq(AdUser::getName, dto.getName()));
        if (aduser == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        //3.对比密码
        String salt = aduser.getSalt();
        String password = dto.getPassword();
        password = DigestUtils.md5DigestAsHex((password + salt).getBytes());
        if (password.equals(aduser.getPassword())){
            //4.返回数据
            Map<String, Object> map = new HashMap<>();
            map.put("token", AppJwtUtil.getToken(aduser.getId().longValue()));
            aduser.setSalt("");
            aduser.setPassword("");
            map.put("user", aduser);
            return ResponseResult.okResult(map);
        }else {
            return ResponseResult.errorResult(AppHttpCodeEnum.LOGIN_PASSWORD_ERROR);
        }
    }
}
