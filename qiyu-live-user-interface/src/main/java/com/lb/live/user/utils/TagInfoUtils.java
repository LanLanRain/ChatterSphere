package com.lb.live.user.utils;

/**
 * @author RainSoul
 * @create 2024-09-25
 */
public class TagInfoUtils {
    /**
     * 检查给定的 TagInfo 是否包含特定的 matchTag
     *
     * @param TagInfo  要检查的标签信息，一个长整型值，其中每个位代表一个标签
     * @param matchTag 要匹配的标签，一个长整型值
     * @return 如果 TagInfo 包含 matchTag 则返回 true，否则返回 false
     */
    public static boolean isContainTag(Long TagInfo, Long matchTag) {
        return TagInfo != null && matchTag != null && (TagInfo & matchTag) == TagInfo;
    }
}
