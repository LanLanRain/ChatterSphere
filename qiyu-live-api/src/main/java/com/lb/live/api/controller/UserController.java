package com.lb.live.api.controller;

import com.lb.live.user.dto.UserDTO;
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
@RequestMapping("/user")
public class UserController {

    @DubboReference(timeout = 5000)
    private IUserRpc userRpc;

    @GetMapping("/updateUserInfo")
    public boolean updateUserInfo(Long userId) {
        UserDTO userDTO = new UserDTO();
        userDTO.setUserId(userId);
        userDTO.setNickName("cdm");
        userDTO.setSex(1);
        return userRpc.updateUserInfo(userDTO);
    }
}
