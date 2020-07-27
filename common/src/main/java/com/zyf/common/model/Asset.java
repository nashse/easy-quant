package com.zyf.common.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 合约账户信息类
 * @author yuanfeng.z
 * @date 2019/6/28 11:37
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public class Asset implements Serializable {

    private static final long serialVersionUID = 1806860024261844247L;

    /**
     * 原始数据，以防定义字段不够用
     */
    private String originData;

    /**
     * 可用余额
     */
    private BigDecimal balance;

    /**
     * 冻结金额
     */
    private BigDecimal freeze;

    /**
     * profit
     */
    private BigDecimal profit;

    /**
     * 总余额
     */
    private BigDecimal totalBalance;

    /**
     * btc价值
     */
    private BigDecimal BTCValue;

    /**
     * cny价值
     */
    private BigDecimal CNYValue;

    /**
     * usdt价值
     */
    private BigDecimal USDValue;

    /**
     * 保证金
     */
    private BigDecimal margin;

    /**
     * 冻结保证金
     */
    private BigDecimal imargin;

    /**
     * btc保证金
     */
    private BigDecimal BTCMargin;

    /**
     * btc可转
     */
    private BigDecimal BTCTransfer;

    /**
     * btc可用
     */
    private BigDecimal BTCAvailable;

    /**
     * btc冻结
     */
    private BigDecimal BTCFrozen;

    /**
     * btc总余额
     */
    private BigDecimal BTCTotalBalance;

    /**
     * BCH保证金
     */
    private BigDecimal BCHMargin;

    /**
     * BCH可转
     */
    private BigDecimal BCHTransfer;

    /**
     * BCH可用
     */
    private BigDecimal BCHAvailable;

    /**
     * BCH冻结
     */
    private BigDecimal BCHFrozen;

    /**
     * BCH总余额
     */
    private BigDecimal BCHTotalBalance;

    /**
     * ETH保证金
     */
    private BigDecimal ETHMargin;

    /**
     * ETH可转
     */
    private BigDecimal ETHTransfer;

    /**
     * ETH可用
     */
    private BigDecimal ETHAvailable;

    /**
     * ETH冻结
     */
    private BigDecimal ETHFrozen;

    /**
     * ETH总余额
     */
    private BigDecimal ETHTotalBalance;

}
