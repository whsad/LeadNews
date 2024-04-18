package com.heima.wemedia.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.heima.common.constants.AdminConstants;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.model.common.dtos.PageResponseResult;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.common.enums.AppHttpCodeEnum;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.model.wemedia.pojos.WmNews;
import com.heima.wemedia.mapper.WmChannelMapper;
import com.heima.wemedia.service.WmChannelService;

import com.heima.wemedia.service.WmNewsService;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.regex.Pattern;


@Service
@Transactional
public class WmChannelServiceImpl extends ServiceImpl<WmChannelMapper, WmChannel> implements WmChannelService {

    @Autowired
    private WmNewsService wmNewsService;

    /**
     * 查询所有频道
     * @return
     */
    @Override
    public ResponseResult findChannelAll() {
        return ResponseResult.okResult(list());
    }

    /**
     * 频道名称模糊擦分页查询
     * @param dto
     * @return
     */
    @Override
    public ResponseResult searchChannelList(ChannelDto dto) {
        //1.检查分页
        dto.checkParam();

        //2.分页查询
        Page page = new Page(dto.getPage(), dto.getSize());
        LambdaQueryWrapper<WmChannel> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        //模糊搜索
        if (StringUtils.isNotBlank(dto.getName())){
            lambdaQueryWrapper.like(WmChannel::getName, dto.getName());
        }
        //按照创建时间倒序排序
        lambdaQueryWrapper.orderByDesc(WmChannel::getCreatedTime);
        page = page(page, lambdaQueryWrapper);
        //结果返回
        PageResponseResult responseResult = new PageResponseResult(dto.getPage(), dto.getSize(), (int) page.getTotal());
        responseResult.setData(page.getRecords());
        return responseResult;
    }

    /**
     * 删除频道
     * @param id
     * @return
     */
    @Override
    public ResponseResult delChannel(Integer id) {
        //1.校验参数
        if (id == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.判断是否存在
        WmChannel wmChannel = getById(id);
        if (wmChannel == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_NOT_EXIST);
        }
        //3.判断状态
        if (wmChannel.getStatus() == AdminConstants.ENABLE_CHANNEL){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "启用的频道不能删除");
        }
        //4.判断是否被引用
        int count = wmNewsService.count(Wrappers.<WmNews>lambdaQuery().eq(WmNews::getChannelId, id));
        if (count > 0){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "频道被引用不能删除");
        }
        removeById(id);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 保存频道
     *
     * @param adChannel
     * @return
     */
    @Override
    public ResponseResult saveChannel(WmChannel adChannel) {
        //1.校验参数
        if (StringUtils.isEmpty(adChannel.getName()) || adChannel.getStatus() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.校验频道名称是否已存在
        WmChannel wmChannel = getOne(Wrappers.<WmChannel>lambdaQuery().eq(WmChannel::getName, adChannel.getName()));
        if (wmChannel != null){
            return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST);
        }
        //3.设置初始值
        adChannel.setIsDefault(true);
        initChannel(adChannel);
        save(adChannel);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }

    /**
     * 校验排序
     * @param adChannel
     */
    private void initChannel(WmChannel adChannel) {
        String regex = "^([01]?[0-9]{1,2}|2[0-4][0-9]|25[0-5])$";
        Pattern compile = Pattern.compile(regex);
        if (adChannel.getOrd() == null || !compile.matcher(adChannel.getOrd().toString()).matches()){
            adChannel.setOrd(0);
        }
        adChannel.setCreatedTime(new Date());
    }

    /**
     * 修改频道
     *
     * @param adChannel
     * @return
     */
    @Override
    public ResponseResult updateChannel(WmChannel adChannel) {
        //1.校验参数
        if (StringUtils.isEmpty(adChannel.getName()) || adChannel.getStatus() == null || adChannel.getId() == null){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID);
        }
        //2.查询原始的频道信息
        WmChannel originalChannel = getOne(Wrappers.<WmChannel>lambdaQuery().eq(WmChannel::getId, adChannel.getId()));
        if (!originalChannel.getName().equals(adChannel.getName())){
            //3.校验名称是否重复
            WmChannel wmChannel = getOne(Wrappers.<WmChannel>lambdaQuery().eq(WmChannel::getName, adChannel.getName()));
            if (wmChannel != null){
                return ResponseResult.errorResult(AppHttpCodeEnum.DATA_EXIST);
            }
        }
        //4.判断频道是否被引用
        int count = wmNewsService.count(Wrappers.<WmNews>lambdaQuery().eq(WmNews::getChannelId, adChannel.getId()));
        if (count > 0 && adChannel.getStatus().equals(AdminConstants.FORBIDDEN_CHANNEL)){
            return ResponseResult.errorResult(AppHttpCodeEnum.PARAM_INVALID, "频道被引用不能禁用");
        }
        initChannel(adChannel);
        //5.保存
        updateById(adChannel);
        return ResponseResult.okResult(AppHttpCodeEnum.SUCCESS);
    }


}
