package com.zyf.common.model.option;

import lombok.*;

import java.math.BigDecimal;
/**
 * 期权合约类
 * @author yuanfeng.z
 * @date 2020/7/27 15:29
 */
@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@NoArgsConstructor
public class Instrument {

    public enum Type {
        //期货
        FUTURE,
        //期权
        OPTION
    }

    public enum Settle {
        DAY,
        MONTH,
        WEEK,
        NEXT_WEEK,
        QUARTER,
        PERPETUAL
    }

    public enum OptionType {
        PUT,
        CALL
    }

    /**
     * 原始数据，以防定义字段不够用
     */
    private String originData;

    /**
     * 名称
     */
    private String name;

    /**
     * 类型
     */
    private Type type;

    /**
     * 币
     */
    private String baseCurrency;

    /**
     * 计价
     */
    private String countCurrency;

    /**
     * 最小单位
     */
    private BigDecimal minTradeSize;

    /**
     * 精度，估计是计价精度
     */
    private Integer countPrecision;

    private BigDecimal tickSize;

    /**
     * 状态
     */
    private Boolean active;

    /**
     * 结算类型
     */
    private Settle settle;

    /**
     * 创建时间
     */
    private Long created;

    /**
     * 到期时间
     */
    private Long expired;

    /**
     * 行权价
     */
    private BigDecimal strike;

    /**
     * 隐含波动率
     */
    private BigDecimal iv;

    public Instrument(String originData, String name, Type type, String baseCurrency, String countCurrency,
                      BigDecimal minTradeSize, Integer countPrecision, BigDecimal tickSize, Boolean active,
                      BigDecimal iv, Settle settle, BigDecimal strike, Long created, Long expired) {
        this.originData = originData;
        this.name = name;
        this.type = type;
        this.baseCurrency = baseCurrency;
        this.baseCurrency = baseCurrency;
        this.countCurrency = countCurrency;
        this.minTradeSize = minTradeSize;
        this.countPrecision = countPrecision;
        this.tickSize = tickSize;
        this.active = active;
        this.iv = iv;
        this.settle = settle;
        this.strike = strike;
        this.created = created;
        this.expired = expired;
    }
}
