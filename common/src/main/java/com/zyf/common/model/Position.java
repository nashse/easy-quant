package com.zyf.common.model;

import com.zyf.common.model.enums.Side;
import com.zyf.common.model.enums.State;
import com.zyf.common.model.enums.Type;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 持仓类
 * @author yuanfeng.z
 * @date 2019/6/29 10:14
 */
@Getter
@ToString
@Data
@EqualsAndHashCode
public class Position implements Serializable {
    private static final long serialVersionUID = -5321844919458631192L;

    /**
     * 原始数据，以防定义字段不够用
     */
    private final String originData;

    /**
     * 时间戳
     */
    private final Long timestamp;

    /**
     * 合约名
     */
    private final String instrument;

    /**
     * 预估强平价
     */
    private final BigDecimal liquidationPrice;

    /**
     * 持仓数量
     */
    private final BigDecimal quantity;

    /**
     * 可平数量
     */
    private final BigDecimal availQuantity;

    /**
     * 开仓平均价
     */
    private final BigDecimal avgCost;

    /**
     * 	结算基准价
     */
    private final BigDecimal settlementPrice;

    /**
     * 已结算收益
     */
    private final BigDecimal settledPnl;

    /**
     * 未实现盈亏
     */
    private final BigDecimal unrealizedPnl;

    /**
     * 最新成交价
     */
    private final BigDecimal last;

    /**
     * 保证金
     */
    private final BigDecimal margin;

    /**
     * 	方向
     */
    private final Side side;
}
