package com.zyf.common.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 成交明细类
 * @author yuanfeng.z
 * @date 2019/6/29 11:26
 */
@Getter
@ToString
@EqualsAndHashCode
public class Trade implements Serializable {
    private static final long serialVersionUID = -211250079118365083L;

    /**
     * 原始数据，以防定义字段不够用
     */
    private final String originData;

    /**
     * 时间戳
     */
    private final Long timestamp;

    /**
     * 订单id
     */
    private final String orderId;

    /**
     * 成交id
     */
    private final String dealId;

    /**
     * 价格
     */
    private final BigDecimal price;

    /**
     * 数量
     */
    private final BigDecimal quantity;

    /**
     * 手续费
     */
    private final BigDecimal fee;

    /**
     * 手续费货币
     */
    private final String feeCurrency;

    public Trade(String originData, Long timestamp, String orderId, String dealId,
                       BigDecimal price, BigDecimal quantity, BigDecimal fee, String feeCurrency) {
        this.originData = originData;
        this.timestamp = timestamp;
        this.orderId = orderId;
        this.dealId = dealId;
        this.price = price;
        this.quantity = quantity;
        this.fee = fee;
        this.feeCurrency = feeCurrency;
    }
}
