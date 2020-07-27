package com.zyf.common.util;

import java.math.BigDecimal;
import java.util.Arrays;

/**
 * BigDecimal工具类
 */
public class BigDecimalUtil {
    public static BigDecimal add(BigDecimal a, BigDecimal b) {
        return a.add(b);
    }
    public static BigDecimal add(BigDecimal... a) {
        if (null != a) {
            if (0 == a.length) { return null; }
            BigDecimal r = a[0];
            for (int i =  1; i < a.length; i++) { r = add(r, a[i]); }
            return r;
        }
        return null;
    }

    /**
     * 减法
     * @param a
     * @param b
     * @return
     */
    public static BigDecimal sub(BigDecimal a, BigDecimal b) {
        return a.subtract(b);
    }
    public static BigDecimal sub(BigDecimal... a) {
        if (null != a) {
            if (0 == a.length) { return null; }
            BigDecimal r = a[0];
            for (int i =  1; i < a.length; i++) { r = sub(r, a[i]); }
            return r;
        }
        return null;
    }

    public static BigDecimal mul(BigDecimal a, BigDecimal b) {
        return a.multiply(b);
    }
    public static BigDecimal div(BigDecimal a, BigDecimal b) {
        return div(a, b, 8);
    }
    public static BigDecimal div(BigDecimal a, BigDecimal b, int scale) {
        return a.divide(b, scale, BigDecimal.ROUND_DOWN);
    }
    private static BigDecimal NEG = new BigDecimal("-1");
    public static BigDecimal neg(BigDecimal a) {
        return NEG.multiply(a);
    }

    private static Boolean exist(BigDecimal a, BigDecimal b) {
        if (null != a) {
            if (null != b) {
                return null;
            } else {
                return false;
            }
        } else {
            if (null != b) {
                return null;
            } else {
                return true;
            }
        }
    }

    public static boolean equal(BigDecimal a, BigDecimal b) {
        Boolean result = exist(a, b);
        return null != result ? result : 0 == a.compareTo(b);
    }
    public static boolean less(BigDecimal a, BigDecimal b) {
        Boolean result = exist(a, b);
        return null != result ? result : a.compareTo(b) < 0;
    }
    public static boolean lessOrEqual(BigDecimal a, BigDecimal b) {
        Boolean result = exist(a, b);
        return null != result ? result : a.compareTo(b) <= 0;
    }
    public static boolean greater(BigDecimal a, BigDecimal b) {
        Boolean result = exist(a, b);
        return null != result ? result : 0 < a.compareTo(b);
    }
    public static boolean greaterOrEqual(BigDecimal a, BigDecimal b) {
        Boolean result = exist(a, b);
        return null != result ? result : 0 <= a.compareTo(b);
    }

    public static BigDecimal avg(BigDecimal... num) {
        BigDecimal count = new BigDecimal(num.length);
        return div(Arrays.stream(num).reduce(BigDecimal.ZERO, BigDecimal::add), count, 16);
    }

    public static BigDecimal max(BigDecimal... num) {
        return Arrays.stream(num).max(BigDecimal::compareTo).get();
    }

    public static BigDecimal min(BigDecimal... num) {
        return Arrays.stream(num).min(BigDecimal::compareTo).get();
    }

    /**
     * 求余
     * @param dividend 被除数
     * @param divisor 除数
     * @return BigDecimal[0]：商 BigDecimal[1]：余
     */
    public static BigDecimal[] divideAndRemainder(BigDecimal dividend, BigDecimal divisor) {
        if (divisor == null || BigDecimal.ZERO.equals(divisor)) {
            return new BigDecimal[2];
        }
        return dividend.divideAndRemainder(divisor);
    }

    public static BigDecimal usdUp(BigDecimal usd) {
        return usd.setScale(2, BigDecimal.ROUND_UP);
    }

    public static BigDecimal usdDown(BigDecimal usd) {
        return usd.setScale(2, BigDecimal.ROUND_DOWN);
    }

    public static BigDecimal btcUp(BigDecimal btc) {
        return btc.setScale(8, BigDecimal.ROUND_UP);
    }

    public static BigDecimal btcDown(BigDecimal btc) {
        return btc.setScale(8, BigDecimal.ROUND_DOWN);
    }

    /**
     * <p>
     *     判断正负，正数直接返回，负数转正数返回
     * </p>
     * */
    public static BigDecimal positive_number(BigDecimal decimal){
        int i=decimal.compareTo(BigDecimal.ZERO);
        if (i>=0){
            return decimal;
        }
        return decimal.negate();
    }

    /**
     * <p>
     *     判断正负，正数返true，负数返false
     * </p>
     * */
    public static Boolean plus_or_minus(BigDecimal decimal){
        return decimal.compareTo(BigDecimal.ZERO)>= 0;
    }

}

