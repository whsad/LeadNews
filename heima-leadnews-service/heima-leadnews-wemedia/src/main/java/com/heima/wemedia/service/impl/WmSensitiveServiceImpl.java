package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.dtos.SensitiveDto;
import com.heima.model.wemedia.pojos.WmSensitive;
import com.heima.wemedia.mapper.WmSensitiveMapper;
import com.heima.wemedia.service.WmSensitiveService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;

@Service
@Transactional
public class WmSensitiveServiceImpl extends ServiceImpl<WmSensitiveMapper, WmSensitive> implements WmSensitiveService {

    /**
     * 查询列表
     * @param dto
     * @return
     */
    @Override
    public ResponseResult searchSensitiveList(SensitiveDto dto) {
        //1.设置分页
        dto.checkParam();

        //2.分页查询
        Page page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmSensitive> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //模糊搜索
        if (StringUtils.isNotBlank(dto.getName())){
            lambdaQueryWrapper.like(WmSensitive::getSensitives, dto.getName());
        }
        //按照创建时间倒序排序
        lambdaQueryWrapper.orderByDesc(WmSensitive::getCreatedTime);
        page = page(page, lambdaQueryWrapper);
        //结果返回
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * 保存敏感词
     * @param adSensitive
     * @return
     */
    @Override
    public ResponseResult saveSensitive(WmSensitive adSensitive) {
        //1.校验参数
        if (StringUtils.isBlank(adSensitive.getSensitives())){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if (adSensitive.getSensitives().length() > 10){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "敏感词最多输入10位");
        }
        WmSensitive sensitive = getOne(Wrappers.<WmSensitive>lambdaQuery().eq(WmSensitive::getSensitives, adSensitive.getSensitives()));
        //2.判断是否已存在
        if (sensitive != null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST);
        }
        //3.封装返回
        adSensitive.setCreatedTime(new Date());
        save(adSensitive);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 修改敏感词
     * @param adSensitive
     * @return
     */
    @Override
    public ResponseResult updateSensitive(WmSensitive adSensitive) {
        //1.校验参数
        if (StringUtils.isBlank(adSensitive.getSensitives()) || adSensitive.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        if (adSensitive.getSensitives().length() > 10){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "敏感词最多输入10位");
        }
        //2.获取原始数据
        WmSensitive originalSensitive = getById(adSensitive.getId());
        if (originalSensitive == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }

        if (originalSensitive.getSensitives().equals(adSensitive.getSensitives())){
            return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
        }

        //3.校验敏感词是否已存在
        WmSensitive sensitive = getOne(Wrappers.<WmSensitive>lambdaQuery().eq(WmSensitive::getSensitives, adSensitive.getSensitives()));
        if (sensitive != null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST);
        }
        //4.修改
        adSensitive.setCreatedTime(new Date());
        updateById(adSensitive);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

}
