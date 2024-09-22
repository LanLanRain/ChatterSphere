package com.lb.live.api.controller;

import com.lb.live.user.interfaces.rpc.IUserRpc;
import org.apache.dubbo.config.annotation.DubboReference;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author RainSoul
 * @create 2024-09-22
 */
@RestController
@RequestMapping("/test")
public class TestControllert {

    @DubboReference
    private IUserRpc userRpc;

    @GetMapping("/dubbo")
    public String test(){
        return userRpc.test();
    }
}
