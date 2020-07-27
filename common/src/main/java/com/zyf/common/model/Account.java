package com.zyf.common.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;

/**
 * 币对账户信息类
 * @author yuanfeng.z
 * @date 2019/6/28 11:37
 */
@Getter
@ToString
@EqualsAndHashCode
public class Account implements Serializable {
    private static final long serialVersionUID = 1806860024261444247L;

    /**
     * 原始数据，以防定义字段不够用
     */
    private final String originData;

    /**
     * 币对
     */
    private final String symbol;

    /**
     * 左币种账户信息
     */
    private final Balance lCurrency;

    /**
     * 右币种账户信息
     */
    private final Balance rCurrency;


    public Account(String originData, String symbol, Balance lCurrency, Balance rCurrency) {
        this.originData = originData;
        this.symbol = symbol;
        this.lCurrency = lCurrency;
        this.rCurrency = rCurrency;
    }
}
