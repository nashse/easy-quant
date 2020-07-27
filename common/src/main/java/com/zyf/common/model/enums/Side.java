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


    BUY_CLOSE("buy_close"),


    SELL_CLOSE("sell_close");


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
        }else if (BUY_CLOSE.value.equals(side)) {
            return SELL;
        }else if (SELL_CLOSE.value.equals(side)) {
            return SELL;
        }
        return null;
    }

}
