package com.lb.live.user.interfaces;

import com.lb.live.user.dto.UserDTO;

/**
 * @author RainSoul
 * @create 2024-09-22
 */
public interface IUserRpc {

    UserDTO getUserById(Long id);

    boolean updateUserInfo(UserDTO userDTO);

    boolean insertOne(UserDTO userDTO);
}
