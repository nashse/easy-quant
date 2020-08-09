package com.zyf.common.model.enums;

/**
 * 证券类型
 *
 * @author yuanfeng.z
 * @date 2020/7/29 21:44
 */
public enum  SecuritiesTypeEnum {
    STOCK("stock"),
    FUTURE("future"),
    OPTION("option");

    private String value;

    SecuritiesTypeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
