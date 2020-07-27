package com.zyf.common.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 币种账户信息类
 * @author yuanfeng.z
 * @date 2019/6/28 11:37
 */
@Getter
@ToString
@EqualsAndHashCode
public class Balance implements Serializable {
    private static final long serialVersionUID = 1806860024261444247L;

    /**
     * 原始数据，以防定义字段不够用
     */
    private final String originData;

    /**
     * 币种
     */
    private final String currency;

    /**
     * 可用资金
     */
    private final BigDecimal available;

    /**
     * 冻结资金
     */
    private final BigDecimal frozen;


    public Balance(String originData, String currency, BigDecimal available, BigDecimal frozen) {
        this.originData = originData;
        this.currency = currency;
        this.available = available;
        this.frozen = frozen;
    }
}
