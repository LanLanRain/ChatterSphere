package com.lb.live.user.provider.dao.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lb.live.user.provider.dao.po.UserTagPO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Update;

/**
 * @author RainSoul
 * @create 2024-09-23
 */
@Mapper
public interface IUserTagMapper extends BaseMapper<UserTagPO> {

    /**
     * 使用或的思路来设置标签，只能允许第一次设置成功
     *
     * @param userId    用户ID，用于定位需要更新的用户记录
     * @param fieldName 字段名，表示要更新的标签字段
     * @param tag       标签值，要设置的具体标签
     * @return 返回更新成功的数量，预期为1
     */
    @Update("update t_user_tag set ${fieldName}=${fieldName} | #{tag} where user_id=#{userId} and ${fieldName} & #{tag}=0")
    int setTag(Long userId, String fieldName, long tag);


    /**
     * 使用先取反在与的思路来取消标签，只能允许第一次删除成功
     *
     * @param userId    用户ID
     * @param fieldName 字段名
     * @param tag       标签值
     * @return 删除成功的数量
     */
    @Update("update t_user_tag set ${fieldName}=${fieldName} &~ #{tag} where user_id=#{userId} and ${fieldName} & #{tag}=#{tag}")
    int cancelTag(Long userId, String fieldName, long tag);

}
