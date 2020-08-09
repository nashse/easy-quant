package com.zyf.common.model.enums;

import cn.hutool.core.util.StrUtil;

/**
 * 买卖方向
 *
 * @author yuanfeng.z
 * @date 2019/6/29 10:30
 */
public enum Side {

    /**
     * 买
     */
    BUY("buy"),

    /**
     * 卖
     */
    SELL("sell"),

    /****期货*****/

    OPEN_LONG("openLong"),

    OPEN_SHORT("openShort"),

    CLOSE_LONG("closeLong"),

    CLOSE_SHORT("closeShort");


    private String value;

    Side(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static Side getByValue(String side) {
        if (StrUtil.isBlank(side)) {
            return null;
        }else if (BUY.value.equals(side)) {
            return BUY;
        } else if (SELL.value.equals(side)) {
            return SELL;
        }else if (OPEN_LONG.value.equals(side)) {
            return OPEN_LONG;
        }else if (OPEN_SHORT.value.equals(side)) {
            return OPEN_SHORT;
        }else if (CLOSE_LONG.value.equals(side)) {
            return CLOSE_LONG;
        }else if (CLOSE_SHORT.value.equals(side)) {
            return CLOSE_SHORT;
        }
        return null;
    }

}
