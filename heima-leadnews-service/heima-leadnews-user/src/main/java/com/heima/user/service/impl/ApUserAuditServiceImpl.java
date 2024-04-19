package com.heima.user.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.apis.wemedia.IWemediaClient;
import com.heima.common.constants.UserConstants;
import com.heima.common.constants.WemediaConstants;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.user.dtos.AuthDto;
import com.heima.model.user.pojos.ApUser;
import com.heima.model.user.pojos.ApUserRealname;
import com.heima.model.wemedia.pojos.WmUser;
import com.heima.user.mapper.ApUserAuditMapper;
import com.heima.user.mapper.ApUserMapper;
import com.heima.user.service.ApUserAuditService;
import com.heima.user.service.ApUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class ApUserAuditServiceImpl extends ServiceImpl<ApUserAuditMapper, ApUserRealname> implements ApUserAuditService {

    @Autowired
    private ApUserService apUserService;

    @Autowired
    private IWemediaClient wemediaClient;

    @Autowired
    private ApUserMapper apUserMapper;
    /**
     * 查询列表
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult searchUser(AuthDto dto) {
        if (dto == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //1.检查分页
        dto.checkParam();

        //2.分页查询
        Page page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<ApUserRealname> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //根据状态精确查询
        if (dto.getStatus() != null){
            lambdaQueryWrapper.eq(ApUserRealname::getStatus, dto.getStatus());
        }
        page = page(page, lambdaQueryWrapper);
        //结果返回
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * 审核成功或失败
     *
     * @param dto
     * @return
     */
    @Override
    public ResponseResult authFailOrPass(AuthDto dto, Short status) {
        //1.校验参数
        if (dto.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }

        //2.修改审核状态
        ApUserRealname realname = new ApUserRealname();
        realname.setUserId(dto.getId());
        realname.setStatus(status);
        if (StringUtils.isNotBlank(dto.getMsg())){
            realname.setReason(dto.getMsg());
        }
        updateById(realname);
        //3.如果审核状态是9，就是成功，需要创建自媒体账户
        if (status.equals(UserConstants.PASS_AUTH)){
            ResponseResult responseResult = createWmUserAndAuth(dto);
            if (responseResult != null){
                return responseResult;
            }
        }
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 创建自媒体用户
     * @param dto
     */
    private ResponseResult createWmUserAndAuth(AuthDto dto) {
        //1.查询用户认证信息
        ApUserRealname realname = getById(dto.getId());
        if (realname == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //2.查询app端用户信息
        ApUser apUser = apUserService.getById(realname.getUserId());
        if (apUser == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //3.创建自媒体用户
        WmUser wmUser = wemediaClient.findWmUserByName(apUser.getName());
        if (wmUser == null){
            wmUser = new WmUser();
            wmUser.setApUserId(apUser.getId());
            wmUser.setCreatedTime(new Date());
            wmUser.setName(apUser.getName());
            wmUser.setPassword(apUser.getPassword());
            wmUser.setSalt(apUser.getSalt());
            wmUser.setPhone(apUser.getPhone());
            wmUser.setStatus(9);
            wemediaClient.saveWmUser(wmUser);
        }
        apUser.setFlag((short) 1);
        apUserMapper.updateById(apUser);
        return null;
    }
}
