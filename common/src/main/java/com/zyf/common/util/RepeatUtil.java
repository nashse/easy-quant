package com.zyf.common.util;

import java.util.function.Function;

/**
 * 重复调用函数工具类
 * @author yuanfeng.z
 * @date 2020/7/17 0:31
 */
public class RepeatUtil {

    public static <T, R> R repeat(T t, Function<T, R> func, int frequency) {
        while (frequency != 0) {
            try {
                return func.apply(t);
            } catch (Exception e) {
                frequency--;
            }
        }
        return null;
    }

    public static <T, R> R repeat(T t, Function<T, R> func) {
        int frequency = 2;
        while (frequency != 0) {
            try {
                return func.apply(t);
            } catch (Exception e) {
                frequency--;
            }
        }
        return null;
    }
}
