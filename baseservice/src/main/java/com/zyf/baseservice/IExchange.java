package com.zyf.baseservice;

import com.zyf.common.model.*;
import com.zyf.common.model.enums.Side;
import com.zyf.common.model.enums.Type;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 交易所接口
 * @author yuanfeng.z
 * @date 2019/6/28 15:33
 */
public interface IExchange {
    /**
     * 获取ticker
     * @param symbol 币对
     * @return
     */
    Ticker getTicker(String symbol);

    /**
     * 获取kline
     * @param symbol 币对
     * @return
     */
    List<Kline> getKline(String symbol);

    /**
     * 获取深度
     * @param symbol 币对
     * @return
     */
    Depth getDepth(String symbol);

    /**
     * 获取精度
     * @return
     */
    Map<String, Precision> getPrecisions();

    /**
     * 获取账户信息
     * @param symbol
     * @return
     */
    Account getAccount(String symbol);

    /**
     * 获取合约账户信息
     * @return
     */
    Asset getAsset();

    /**
     * 市价买入
     * @param symbol 币对
     * @param cost 费用
     * @return 订单id
     */
    String buyMarket(String symbol, BigDecimal cost);

    /**
     * 限价买入
     * @param symbol 币对
     * @param price 价格
     * @param quantity 数量
     * @return 订单id
     */
    String buyLimit(String symbol, BigDecimal price, BigDecimal quantity);


    /**
     * 限价买入
     * @param symbol 币对
     * @param price 价格
     * @param quantity 数量
     * @param leverage 杠杆
     * @return 订单id
     */
    String buyLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage);


    /**
     * 限价买入
     * @param symbol 币对
     * @param price 价格
     * @param quantity 数量
     * @param leverage 杠杆
     * @return 订单id
     */
    String buyLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage, Side side);

    /**
     * 买入止损单
     * @param symbol 币对
     * @param price 价格
     * @param quantity 数量
     * @return 订单id
     */
    String buyStopMarket(String symbol, BigDecimal price, BigDecimal quantity);

    /**
     * 市价卖出
     * @param symbol 币对
     * @param quantity 卖出数量
     * @return 订单id
     */
    String sellMarket(String symbol, BigDecimal quantity);

    /**
     * 限价卖出
     * @param symbol 币对
     * @param price 价格
     * @param quantity 数量
     * @return 订单id
     */
    String sellLimit(String symbol, BigDecimal price, BigDecimal quantity);

    /**
     * 限价卖出
     * @param symbol 币对
     * @param price 价格
     * @param quantity 数量
     * @param leverage 杠杆
     * @return 订单id
     */
    String sellLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage);


    /**
     * 限价卖出
     * @param symbol 币对
     * @param price 价格
     * @param quantity 数量
     * @param leverage 杠杆
     * @return 订单id
     */
    String sellLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage, Side side);

    /**
     * 卖出止损单
     * @param symbol 币对
     * @param price 价格
     * @param quantity 数量
     * @return 订单id
     */
    String sellStopMarket(String symbol, BigDecimal price, BigDecimal quantity);

    /**
     * 根据订单id撤单
     * @param symbol 币对
     * @param orderId 订单id
     * @return
     */
    Boolean cancelOrder(String symbol, String orderId);

    /**
     * 批量撤单
     * @param symbol 币对
     * @param ids
     * @return
     */
     Boolean cancelOrders(String symbol, List<String> ids);

    /**
     * 获取所有的订单
     * @param symbol 币对
     * @return 订单列表
     */
     List<Order> getOrders(String symbol);

    /**
     * 获取所有的正在挂的订单
     * @param symbol 币对
     * @return 订单列表
     */
    List<Order> getPendingOrders(String symbol);

    /**
     * 获取指定id的订单信息
     * @param symbol 币对
     * @param orderId 订单id
     * @return 订单
     */
     Order getOrder(String symbol, String orderId);

     /**
      * 获取成交明细
      * @param symbol 币对
      * @param orderId 订单id
      * @return List<Trade>
      */
     List<Trade> getTrade(String symbol, String orderId);

    /**
     * 获取合约成交明细
     * @param symbol 币对
     * @return List<Trade>
     */
    List<InstrumentTrade> getInstrumentTrade(String symbol);

    /**
     * 根据合约获取当前仓位持仓张数
     * @param symbol 合约
     * */
     BigDecimal getContract(String symbol);

    /**
     * 根据合约获取未成交订单
     * @param symbol 合约
     * */
     List<Order> get_unsettled_orders(String symbol);

    /**
     * 根据合约撤掉所有订单 ok
     * */
     Boolean cancelAll(String symbol);

    /**
     * 撤销交易所所有订单 ok
     * */
    Boolean cancelAll();

    /**
     * 合约批量下单
     * */
     Boolean placeOrders(String symbol, List<Order> orders, Integer leverage);

    /**
     * 平掉所有仓位
     * @return
     */
     String closePosition(String symbol, Type type, BigDecimal price);

    /**
     * 获取所有持仓
     * @param symbol
     * @return
     */
     List<Order> getPosition(String symbol);
}
