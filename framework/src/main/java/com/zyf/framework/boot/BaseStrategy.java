package com.zyf.framework.boot;

import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.IExchange;
import com.zyf.baseservice.IMdExchange;
import com.zyf.baseservice.ITradeExchange;
import com.zyf.common.model.Depth;
import com.zyf.marketdata.factory.MdExchangeFactory;
import com.zyf.marketdata.proxy.MdExchangeProxy;
import com.zyf.trade.factory.TradeExchangeFactory;
import com.zyf.trade.proxy.TradeExchangeProxy;

import java.math.BigDecimal;
import java.util.logging.Logger;

/**
 * 策略基类
 *
 * @author yuanfeng.z
 * @date 2020/7/22 19:01
 */
public abstract class BaseStrategy {
    /**
     * 日志
     */
    protected Logger log;

    /**
     * 交易所名字
     */
    protected String exchangeName;

    /**
     * 本实例id(配置名称)
     */
    protected String id;

    /**
     * 币对
     */
    protected String symbol;

    /**
     * 本实例配置
     */
    protected JSONObject config;

    /**
     * 公有交易所类
     */
    protected IMdExchange mdE;

    /**
     * 私有交易所类
     */
    protected ITradeExchange tradeE;

    /**
     * 初始策略运行中数据文件路径
     */
    protected String storeFile;

    /**
     * 0：初始化策略 1：恢复策略
     */
    protected Integer bInit = 0;

    /**
     * 初始化方法
     */
    protected abstract void init();

    /**
     * 恢复策略
     */
    protected abstract void recovery();

    /**
     * 默认循环
     */
    protected void run() {
    }

    /**
     * 退出方法
     */
    public void destroy() {
    }

    /**
     * 获取买一
     * @return
     */
    public BigDecimal getBid1() {
        Depth depth = mdE.getDepth(symbol);
        BigDecimal bid1 = depth.getBids().get(0).getPrice();
        return bid1;
    }

    /**
     * 获取卖一
     * @return
     */
    public BigDecimal getAsk1() {
        Depth depth = mdE.getDepth(symbol);
        BigDecimal ask1 = depth.getAsks().get(0).getPrice();
        return ask1;
    }

    @Override
    public String toString() {
        return "id: " + id +
                " symbol: " + symbol +
                " exchangeName: " + exchangeName +
                " config: " + config.toJSONString() +
                " mdE: " + mdE.toString() +
                " tradeE: " + tradeE.toString();
    }
}
