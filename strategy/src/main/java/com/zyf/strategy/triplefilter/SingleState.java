package com.zyf.strategy.triplefilter;

/**
 * 信号状态
 * @author yuanfeng.z
 * @date 2020/9/8 17:16
 */
public enum SingleState {

    LONG("long"),
    SHORT("short"),
    NOT_SINGLE("notSignle");

    private String value;

    SingleState(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
