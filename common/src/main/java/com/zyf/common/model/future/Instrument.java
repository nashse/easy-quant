package com.zyf.common.model.future;

import lombok.*;

import java.math.BigDecimal;
/**
 * 期货合约类
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
        MONTH,
        WEEK,
        QUARTER,
        PERPETUAL
    }

    public enum OptionType {
        PUT,
        CALL
    }

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
     * 期权类型
     */
    private OptionType optionType;

    public Instrument(String name, Type type, String baseCurrency, String countCurrency,
                      BigDecimal minTradeSize, Integer countPrecision, BigDecimal tickSize, Boolean active,
                      Settle settle, Long created, Long expired) {
        this.name = name;
        this.type = type;
        this.baseCurrency = baseCurrency;
        this.baseCurrency = baseCurrency;
        this.countCurrency = countCurrency;
        this.minTradeSize = minTradeSize;
        this.countPrecision = countPrecision;
        this.tickSize = tickSize;
        this.active = active;
        this.settle = settle;
        this.created = created;
        this.expired = expired;
    }
}
