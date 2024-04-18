package com.heima.wemedia.feign;

import com.heima.apis.wemedia.IWemediaClient;
import com.heima.wemedia.service.WmChannelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class WemediaClient implements IWemediaClient {

    @Autowired
    private WmChannelService wmChannelService;


}
