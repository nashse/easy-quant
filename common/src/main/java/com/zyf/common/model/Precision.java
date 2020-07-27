package com.zyf.common.model;

import lombok.Getter;
import lombok.ToString;

/**
 * 币对精度
 * @author yuanfeng.z
 * @date 2019/6/26 10:54
 */
@Getter
@ToString
public class Precision {

    /**
     * 原始数据，以防定义字段不够用
     */
    private final String originData;

    /**
     * 币对
     */
    private final String symbol;

    /**
     * 左边货币精度（BTC/USDT -> BTC精度）
     */
    private final Integer lPrecision;

    /**
     * 右边货币精度（BTC/USDT -> USDT精度）
     */
    private final Integer rPrecision;

    public Precision(String originData, String symbol, Integer lPrecision, Integer rPrecision) {
        this.originData = originData;
        this.symbol = symbol;
        this.lPrecision = lPrecision;
        this.rPrecision = rPrecision;
    }
}
