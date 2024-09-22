package com.lb.live.user.provider.rpc;

import com.lb.live.user.dto.UserDTO;
import com.lb.live.user.interfaces.rpc.IUserRpc;
import com.lb.live.user.provider.service.IUserService;
import jakarta.annotation.Resource;
import org.apache.dubbo.config.annotation.DubboService;

/**
 * @author RainSoul
 * @create 2024-09-22
 */
@DubboService
public class UserRpcImpl implements IUserRpc {

    @Resource
    private IUserService userService;

    @Override
    public UserDTO getUserById(Long id) {
        return userService.getUserById(id);
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        return userService.updateUserInfo(userDTO);
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        return userService.insertOne(userDTO);
    }
}
