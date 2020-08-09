package com.zyf.strategy;

import com.zyf.common.model.Depth;
import com.zyf.common.model.Order;
import com.zyf.common.util.MathUtil;
import com.zyf.framework.boot.BaseStrategy;
import com.zyf.framework.scheduling.interfaces.ScheduledByZyf;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

/**
 * 网格策略
 * @author yuanfeng.z
 * @date 2020/7/27 1:44
 */
@Slf4j
public class GridStrategy extends BaseStrategy {

    /**
     * 挂单之间差额
     */
    private BigDecimal spreadRate = new BigDecimal(0.03);

    /**
     * btc-usd合约最小单位
     */
    private BigDecimal minUtil = new BigDecimal("0.1");

    /**
     * 挂单数量（委托和计划）
     */
    private int orderNum = 0;

    @Override
    protected void init() {
        log.info("init");

        // 撤单
        tradeE.cancelAll(this.symbol);
        tradeE.triggerCancelAll(symbol);
        //.getTriggerPendingOrders(symbol);
    }

    @Override
    protected void recovery() {

    }

    @ScheduledByZyf(cron = "*/1 * * * * ?")
    private void checkPendingOrder() {
        // 获取挂单
        List<Order> orders = tradeE.getPendingOrders(this.symbol);
        orders.addAll(tradeE.getTriggerPendingOrders(this.symbol));
        this.orderNum = orders.size();
        if ( 1 <= this.orderNum && this.orderNum <= 2) {
            // 撤单
            tradeE.cancelAll(this.symbol);
            tradeE.triggerCancelAll(symbol);
        }
    }

    @Override
    @ScheduledByZyf(cron = "*/1 * * * * ?")
//    @ScheduledByZyf(initialDelay = 0, fixedDelay = 1000)
    protected void run() {
        // 挂单
        Depth d = mdE.getDepth(this.symbol);
        log.info(d.toString());
        BigDecimal askPrice = d.getAsks().get(0).getPrice();
        BigDecimal bidPrice = d.getBids().get(0).getPrice();
        // 获取挂单
        List<Order> orders = tradeE.getPendingOrders(this.symbol);
        orders.addAll(tradeE.getTriggerPendingOrders(this.symbol));
        if ( 1 <= orders.size() && orders.size() <= 2) {
            // 撤单
            tradeE.cancelAll(this.symbol);
            tradeE.triggerCancelAll(this.symbol);
        }

        if (this.orderNum == 0) {
            // 止损
            BigDecimal price = bidPrice.multiply(BigDecimal.ONE.subtract(spreadRate.multiply(new BigDecimal("2")))).setScale(1, RoundingMode.UP);
            price = MathUtil.adjustMinUtil(price, minUtil, RoundingMode.UP);
            id = tradeE.triggerCloseBuy(this.symbol, price, new BigDecimal("1"), 1);
            price = askPrice.multiply(BigDecimal.ONE.add(spreadRate.multiply(new BigDecimal("2")))).setScale(1, RoundingMode.DOWN);
            price = MathUtil.adjustMinUtil(price, minUtil, RoundingMode.DOWN);
            id = tradeE.triggerCloseSell(this.symbol, price, new BigDecimal("1"), 1);

            // 下单
            price = bidPrice.multiply(BigDecimal.ONE.subtract(spreadRate)).setScale(1, RoundingMode.DOWN);;
            price = MathUtil.adjustMinUtil(price, minUtil, RoundingMode.DOWN);
            String id = tradeE.openLong(this.symbol, price, new BigDecimal("1"), 1);
            price = askPrice.multiply(BigDecimal.ONE.add(spreadRate)).setScale(1, RoundingMode.UP);
            price = MathUtil.adjustMinUtil(price, minUtil, RoundingMode.UP);
            id = tradeE.closeLong(this.symbol, price, new BigDecimal("1"), 1);
        }


    }
}
