package com.zyf.common.model.future;

import lombok.*;

import java.math.BigDecimal;

/**
 * 期货合约类
 *
 * @author yuanfeng.z
 * @date 2020/7/27 15:29
 */
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class InstrumentFut {

    /**
     * 原始数据，以防定义字段不够用
     */
    private String originData;

    /**
     * 名称
     */
    private String name;

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

    /**
     * 合约类型: （this_week:当周 next_week:下周 quarter:当季 next_quarter:次季）
     */
    private String contractType;

    /**
     * 创建时间
     */
    private Long created;

    /**
     * 到期时间
     */
    private Long expired;

    public InstrumentFut(String originData, String name, String baseCurrency, String countCurrency,
                         BigDecimal minTradeSize, Integer countPrecision,
                         String contractType, Long created, Long expired) {
        this.originData = originData;
        this.name = name;
        this.baseCurrency = baseCurrency;
        this.baseCurrency = baseCurrency;
        this.countCurrency = countCurrency;
        this.minTradeSize = minTradeSize;
        this.countPrecision = countPrecision;
        this.contractType = contractType;
        this.created = created;
        this.expired = expired;
    }
}
