package com.lb.live.user.provider.service;

import com.lb.live.user.dto.UserDTO;

import java.util.List;
import java.util.Map;

/**
 * @author RainSoul
 * @create 2024-09-22
 */
public interface IUserService {
    /**
     * 根据用户id进行查询
     *
     * @param userId
     * @return
     */
    UserDTO getByUserId(Long userId);

    /**
     * 用户信息更新
     *
     * @param userDTO
     * @return
     */
    boolean updateUserInfo(UserDTO userDTO);

    /**
     * 插入用户信息
     *
     * @param userDTO
     * @return
     */
    boolean insertOne(UserDTO userDTO);

    /**
     * 批量查询用户信息
     *
     * @param userIdList
     * @return
     */
    Map<Long,UserDTO> batchQueryUserInfo(List<Long> userIdList);
}
