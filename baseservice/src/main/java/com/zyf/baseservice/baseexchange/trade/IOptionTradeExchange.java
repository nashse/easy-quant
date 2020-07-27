package com.zyf.baseservice.baseexchange.trade;

import com.zyf.common.model.*;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.List;

/**
 * 期权接口类
 * @author yuanfeng.z
 * @date 2019/7/19 10:22
 */
public interface IOptionTradeExchange {
    /**
     * 获取账户信息
     * @param currency
     * @return
     */
    Balance getBalance(String currency);

    /**
     * 限价买入
     * @param symbol 币对
     * @param price 价格
     * @param quantity 数量
     * @return 订单id
     */
    String buyLimit(String symbol, BigDecimal price, BigDecimal quantity);

    /**
     * 限价卖出
     * @param symbol 币对
     * @param price 价格
     * @param quantity 数量
     * @return 订单id
     */
    String sellLimit(String symbol, BigDecimal price, BigDecimal quantity);

    /**
     * 市价买入
     * @param symbol 币对
     * @param cost 费用
     * @return 订单id
     */
    String buyMarket(String symbol, BigDecimal cost);

    /**
     * 市价卖出
     * @param symbol 币对
     * @param quantity 卖出数量
     * @return 订单id
     */
    String sellMarket(String symbol, BigDecimal quantity);

    Boolean cancelOrder(String symbol, String orderId);

    Boolean cancelOrders(String symbol, List<String> ids);

    List<Order> getOrders_currency(String currency);

    List<Order> getOrders_instrument(String instrument);

    Order getOrder(String orderId) throws UnsupportedEncodingException;

    /**
     * 获取成交明细
     * @param symbol
     * @param orderId
     * @return
     */
    List<Trade> getTrade(String symbol, String orderId);
}
