package com.zyf.framework.boot;

import com.alibaba.fastjson.JSONObject;
import com.zyf.marketdata.proxy.MdExchangeProxy;
import com.zyf.trade.proxy.TradeExchangeProxy;

import java.util.logging.Logger;

/**
 * 策略基类
 *
 * @author yuanfeng.z
 * @date 2020/7/22 19:01
 */
public class BaseStrategy {
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
    protected MdExchangeProxy mdE;

    /**
     * 私有交易所类
     */
    protected TradeExchangeProxy tradeE;

    /**
     * 初始化方法
     */
    protected void init() {
    }

    /**
     * 默认循环
     */
    protected void run() {
    }

    /**
     * 退出方法
     */
    protected void destroy() {
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
