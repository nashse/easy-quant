package com.zyf.common.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;

/**
 * 处理数字工具类
 * @author yuanfeng.z
 * @date 2020/7/31 1:07
 */
public class MathUtil {
    /**
     * 移动小数位
     * @param data 目标数据
     * @param digits 移动位数
     * @return
     */
    public static BigDecimal moveDigits(BigDecimal data, Integer digits) {
        for (int i = 0; i < digits; i++) {
            data = data.multiply(new BigDecimal("0.1"));
        }
        return data;
    }

    /**
     * 调整数据最小单位
     * @param data
     * @param minUtil
     * @return
     */
    public static BigDecimal adjustMinUtil(BigDecimal data, BigDecimal minUtil, final RoundingMode roundingMode) {
        BigDecimal[] arg = BigDecimalUtil.divideAndRemainder(data, minUtil);

        final BigDecimal res = data.subtract(arg[1]);
        // bid、ask区别处理
        if (roundingMode == RoundingMode.UP) {
            return res.add(minUtil);
        } else {
            return res;
        }
    }

    public static Integer randomInt(int seed) {
        Random random = new Random();
        Integer res = random.nextInt(seed);
        return res;
    }
}
