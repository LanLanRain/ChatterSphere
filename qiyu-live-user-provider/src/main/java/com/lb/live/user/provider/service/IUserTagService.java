package com.lb.live.user.provider.service;

import com.lb.live.user.constants.UserTagsEnum;

/**
 * @author RainSoul
 * @create 2024-09-24
 */
public interface IUserTagService {

    /**
     * 设置标签
     *
     * @param userId
     * @param userTagsEnum
     * @return
     */
    boolean setTag(Long userId, UserTagsEnum userTagsEnum);

    /**
     * 取消标签
     *
     * @param userId
     * @param userTagsEnum
     * @return
     */
    boolean cancelTag(Long userId,UserTagsEnum userTagsEnum);

    /**
     * 是否包含某个标签
     *
     * @param userId
     * @param userTagsEnum
     * @return
     */
    boolean containTag(Long userId, UserTagsEnum userTagsEnum);
}
