package com.lb.live.common.interfaces.utils;

import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.BeanUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author RainSoul
 * @create 2024-09-22
 */
public class ConvertBeanUtils {

    /**
     * 将一个对象转成目标对象
     *
     * @param source      源对象
     * @param targetClass 目标对象的类
     * @param <T>         目标对象的泛型类型
     * @return 转换后的目标对象
     */
    public static <T> T convert(Object source, Class<T> targetClass) {
        // 检查源对象是否为null，若为null则直接返回null
        if (source == null) {
            return null;
        }
        // 创建目标对象实例
        T t = newInstance(targetClass);
        // 复制源对象属性到目标对象
        BeanUtils.copyProperties(source, t);
        // 返回转换后的目标对象
        return t;
    }


    /**
     * 将List对象转换成目标对象，注意实现是ArrayList
     *
     * @param targetClass 目标对象的类类型
     * @param <K>         泛型标记，表示源列表中元素的类型
     * @param <T>         泛型标记，表示目标列表中元素的类型
     * @return 转换后的目标对象列表
     */
    public static <K, T> List<T> convertList(List<K> sourceList, Class<T> targetClass) {
        // 检查源列表是否为null，若为null则直接返回null以避免空指针异常
        if (sourceList == null) {
            return null;
        }
        // 初始化目标列表，使用计算得到的容量以减少扩容操作
        List<T> targetList = new ArrayList<>((int) (sourceList.size() / 0.75) + 1);
        // 遍历源列表，将每个元素转换为目标类型后添加到目标列表中
        for (K source : sourceList) {
            targetList.add(convert(source, targetClass));
        }
        // 返回转换后的目标列表
        return targetList;
    }


    /**
     * 创建目标类的新实例
     *
     * @param targetClass 目标类的Class对象
     * @param <T>         目标类的类型
     * @return 目标类的新实例
     * @throws BeanInstantiationException 如果实例化失败，则抛出此异常
     */
    private static <T> T newInstance(Class<T> targetClass) {
        try {
            // 使用目标类的Class对象来创建新实例
            return targetClass.newInstance();
        } catch (Exception e) {
            // 如果实例化过程中发生异常，则抛出自定义异常
            throw new BeanInstantiationException(targetClass, "instantiation error", e);
        }
    }

}
