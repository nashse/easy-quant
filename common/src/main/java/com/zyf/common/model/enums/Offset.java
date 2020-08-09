package com.zyf.common.model.enums;

import cn.hutool.core.util.StrUtil;

/**
 * "open":开 "close":平
 *
 * @author yuanfeng.z
 * @date 2019/6/29 10:30
 */
public enum Offset {

    /**
     * 买
     */
    OPEN("open"),

    /**
     * 卖
     */
    CLOSE("close");


    private String value;

    Offset(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
