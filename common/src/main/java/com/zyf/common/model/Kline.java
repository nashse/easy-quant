package com.zyf.common.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * k线类
 * @author yuanfeng.z
 * @date 2019/6/29 11:34
 */
@Getter
@ToString
@EqualsAndHashCode
public class Kline implements Serializable {
    private static final long serialVersionUID = 8454688387229030586L;

    /**
     * 原始数据，以防定义字段不够用
     */
    private final String originData;

    /**
     * 时间戳
     */
    private final Long timestamp;

    /**
     * 开盘价
     */
    private final BigDecimal open;

    /**
     * 收盘价
     */
    private final BigDecimal close;

    /**
     * 最低价
     */
    private final BigDecimal low;

    /**
     * 最高价
     */
    private final BigDecimal high;

    /**
     * 交易量
     */
    private final BigDecimal volume;

    public Kline(String originData, Long timestamp, BigDecimal open, BigDecimal high,
                 BigDecimal low, BigDecimal close, BigDecimal volume) {
        this.originData = originData;
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.volume = volume;
    }
}
