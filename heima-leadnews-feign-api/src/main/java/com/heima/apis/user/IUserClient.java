package com.heima.apis.user;

import com.heima.model.user.pojos.ApUser;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(value = "leadnews-user")
public interface IUserClient {

    @GetMapping("/user/v1/user/findUserById/{id}")
    ApUser findUserById(@PathVariable("id") Integer id);
}
