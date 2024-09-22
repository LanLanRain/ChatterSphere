package com.lb.live.user.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lb.live.user.provider.dao.po.UserPO;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author RainSoul
 * @create 2024-09-22
 */
@Mapper
public interface IUserMapper extends BaseMapper<UserPO> {
}
