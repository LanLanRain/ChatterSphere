package com.lb.live.user.provider.service.impl;

import com.lb.live.common.interfaces.utils.ConvertBeanUtils;
import com.lb.live.user.dto.UserDTO;
import com.lb.live.user.provider.dao.mapper.IUserMapper;
import com.lb.live.user.provider.dao.po.UserPO;
import com.lb.live.user.provider.service.IUserService;
import jakarta.annotation.Resource;
import org.springframework.stereotype.Service;

/**
 * @author RainSoul
 * @create 2024-09-22
 */
@Service
public class IUserServiceImpl implements IUserService {

    @Resource
    private IUserMapper userMapper;

    @Override
    public UserDTO getUserById(Long id) {
        return ConvertBeanUtils.convert(userMapper.selectById(id), UserDTO.class);
    }

    @Override
    public boolean updateUserInfo(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        userMapper.updateById(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }

    @Override
    public boolean insertOne(UserDTO userDTO) {
        if (userDTO == null || userDTO.getUserId() == null) {
            return false;
        }
        userMapper.insert(ConvertBeanUtils.convert(userDTO, UserPO.class));
        return true;
    }
}
