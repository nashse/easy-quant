package com.zyf.common.model;

import com.zyf.common.model.enums.DealType;
import com.zyf.common.model.enums.Role;
import com.zyf.common.model.enums.Side;
import com.zyf.common.model.enums.Type;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * 合约成交明细
 * @author yuanfeng.z
 * @date 2019/12/23
 */
@Getter
@Setter
public class InstrumentTrade {

    /**
     * 原始数据，以防定义字段不够用
     */
    private String originData;

    /**
     * id
     */
    private Long id;

    /**
     * coin_symbol
     */
    private String coinSymbol;

    /**
     * 合约符号
     */
    private String pair;

    /**
     * 类型，1开仓, 2平仓, 3爆仓, 4减仓
     */
    private DealType dealType;

    /**
     * side 1开多，2开空
     */
    private Integer orderSide;

    /**
     * price_open 委托价格
     */
    private BigDecimal priceOpen;

    /**
     * price 开仓均价
     */
    private BigDecimal price;

    /**
     * price_deal 成交价格
     */
    private BigDecimal priceDeal;

    /**
     * 合约张数
     */
    private BigDecimal contract;

    /**
     * 盈亏
     */
    private BigDecimal profit;

    /**
     * 手续费
     */
    private BigDecimal fee;

    /**
     * 时间
     */
    private Date date;

    /**
     * 费率
     */
    private BigDecimal feeRate;

    /**
     * 角色
     */
    private Role role;

    /**
     * buy or sell
     */
    private Side side;

    /**
     * "limit，"market"或"liquidation"
     */
    private Type type;

}
