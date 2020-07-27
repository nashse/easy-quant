package com.zyf.common.model.future;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 指数类
 * @author yuanfeng.z
 * @date 2019/7/19 9:59
 */
@Getter
@EqualsAndHashCode
@ToString
public class Index {

    /**
     * 时间戳（"2018-08-20T03:44:50.000Z"）
     */
    private Date timestamp;

    /**
     * 合约（"BTCUSD"）
     */
    private String symbol;

    /**
     * 指数合约（".BXBT"）
     */
    private String indexSymbol;

    /**
     * "BSTP"
     */
    private String reference;

    /**
     * 收盘价格
     */
    private BigDecimal closePrice;

    /**
     *
     */
    private BigDecimal price;

    /**
     * 权重（0.5）
     */
    private BigDecimal weight;

    /**
     *
     */
    private Date logged;
}
