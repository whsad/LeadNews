package com.heima.wemedia.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.pojos.WmChannel;


public interface WmChannelService extends IService<WmChannel> {

    /**
     * 查询所有频道
     * @return
     */
    ResponseResult findChannelAll();

    /**
     * 频道名称模糊擦分页查询
     * @param dto
     * @return
     */
    ResponseResult searchChannelList(ChannelDto dto);

    /**
     * 删除频道
     * @param id
     * @return
     */
    ResponseResult delChannel(Integer id);

    /**
     * 保存频道
     * @param adChannel
     * @return
     */
    ResponseResult saveChannel(WmChannel adChannel);

    /**
     * 修改频道
     * @param adChannel
     * @return
     */
    ResponseResult updateChannel(WmChannel adChannel);
}
