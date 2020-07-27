package com.zyf.common.model;


import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
/**
 * 市场心跳类
 * @author yuanfeng.z
 * @date 2019/6/24 18:09
 */
@Getter
@ToString
public class Ticker implements Serializable {
    private static final long serialVersionUID = -6958478794810546513L;

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
     * 24小时量
     */
    private final BigDecimal vol;

    public Ticker(String originData, Long timestamp, BigDecimal open, BigDecimal high,
                   BigDecimal low, BigDecimal close, BigDecimal vol) {
        this.originData = originData;
        this.timestamp = timestamp;
        this.open = open;
        this.high = high;
        this.low = low;
        this.close = close;
        this.vol = vol;
    }
}
