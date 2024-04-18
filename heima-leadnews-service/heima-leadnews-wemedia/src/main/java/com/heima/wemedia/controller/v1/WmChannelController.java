package com.heima.wemedia.controller.v1;

import com.heima.model.common.dtos.ResponseResult;
import com.heima.model.wemedia.dtos.ChannelDto;
import com.heima.model.wemedia.pojos.WmChannel;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/channel/")
public class WmChannelController {

    @Autowired
    private WmChannelService wmChannelService;

    @GetMapping("/channels")
    public ResponseResult findChannelAll(){
        return wmChannelService.findChannelAll();
    }

    /**
     * 频道名称模糊擦分页查询
     * @param dto
     * @return
     */
    @PostMapping("/list")
    public ResponseResult searchChannelList(@RequestBody ChannelDto dto) {
        return wmChannelService.searchChannelList(dto);
    }

    /**
     * 删除频道
     * @param id
     * @return
     */
    @GetMapping("/del/{id}")
    public ResponseResult delChannel(@PathVariable Integer id) {
        return wmChannelService.delChannel(id);
    }

    /**
     * 保存频道
     * @param adChannel
     * @return
     */
    @PostMapping("/save")
    public ResponseResult saveChannel(@RequestBody WmChannel adChannel) {
        return wmChannelService.saveChannel(adChannel);
    }

    /**
     * 修改频道
     * @param adChannel
     * @return
     */
    @PostMapping("/update")
    public ResponseResult updateChannel(@RequestBody WmChannel adChannel) {
        return wmChannelService.updateChannel(adChannel);
    }
}
