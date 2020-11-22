package com.zyf.baseservice.util;

/**
 * 字段工具类
 * @author yuanfeng.z
 * @date 2020/9/19 22:07
 */
public class FieldUtil {

    /**
     * 买卖
     * @param buySell buy sell
     * @return
     */
    public static int buySell(String buySell) {
        if ("buy".equals(buySell)) {
            return 1;
        } else if ("sell".equals(buySell)) {
            return -1;
        } else {
            throw new RuntimeException();
        }
    }

    /**
     * 看涨看跌
     * @param callPut P、C
     * @return
     */
    public static int callPut(String callPut) {
        if ("P".equals(callPut)) {
            return -1;
        } else if ("C".equals(callPut)) {
            return 1;
        } else {
            throw new RuntimeException();
        }
    }
}
