package com.zyf.baseservice;

import com.zyf.common.model.*;
import com.zyf.common.model.enums.Side;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * trade交易所接口
 * <br> 私有接口
 * @author yuanfeng.z
 * @date 2019/6/28 15:33
 */
public interface ITradeExchange {

    /**
     * 获取精度
     *
     * @return
     */
    Map<String, Precision> getPrecisions();

    /**
     * 获取账户信息
     *
     * @param symbol
     * @return
     */
    Account getAccount(String symbol);

    /**
     * 市价买入
     *
     * @param symbol 币对
     * @param cost   费用
     * @return 订单id
     */
    String buyMarket(String symbol, BigDecimal cost);

    /**
     * 限价买入
     *
     * @param symbol   币对
     * @param price    价格
     * @param quantity 数量
     * @return 订单id
     */
    String buyLimit(String symbol, BigDecimal price, BigDecimal quantity);


    /**
     * 限价买入
     *
     * @param symbol   币对
     * @param price    价格
     * @param quantity 数量
     * @param leverage 杠杆
     * @return 订单id
     */
    String buyLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage);


    /**
     * 限价买入
     *
     * @param symbol   币对
     * @param price    价格
     * @param quantity 数量
     * @param leverage 杠杆
     * @return 订单id
     */
    String buyLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage, Side side);

    /**
     * 买入止损单
     *
     * @param symbol   币对
     * @param price    价格
     * @param quantity 数量
     * @return 订单id
     */
    String buyStopMarket(String symbol, BigDecimal price, BigDecimal quantity);

    /**
     * 市价卖出
     *
     * @param symbol   币对
     * @param quantity 卖出数量
     * @return 订单id
     */
    String sellMarket(String symbol, BigDecimal quantity);

    /**
     * 限价卖出
     *
     * @param symbol   币对
     * @param price    价格
     * @param quantity 数量
     * @return 订单id
     */
    String sellLimit(String symbol, BigDecimal price, BigDecimal quantity);

    /**
     * 限价卖出
     *
     * @param symbol   币对
     * @param price    价格
     * @param quantity 数量
     * @param leverage 杠杆
     * @return 订单id
     */
    String sellLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage);


    /**
     * 限价卖出
     *
     * @param symbol   币对
     * @param price    价格
     * @param quantity 数量
     * @param leverage 杠杆
     * @return 订单id
     */
    String sellLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage, Side side);

    /**
     * 卖出止损单
     *
     * @param symbol   币对
     * @param price    价格
     * @param quantity 数量
     * @return 订单id
     */
    String sellStopMarket(String symbol, BigDecimal price, BigDecimal quantity);

    /**
     * 根据订单id撤单
     *
     * @param symbol  币对
     * @param orderId 订单id
     * @return
     */
    Boolean cancelOrder(String symbol, String orderId);

    /**
     * 批量撤单
     *
     * @param symbol 币对
     * @param ids
     * @return
     */
    Boolean cancelOrders(String symbol, List<String> ids);

    /**
     * 获取所有的订单
     *
     * @param symbol 币对
     * @return 订单列表
     */
    List<Order> getOrders(String symbol);

    /**
     * 获取所有的正在挂的订单
     *
     * @param symbol 币对
     * @return 订单列表
     */
    List<Order> getPendingOrders(String symbol);

    /**
     * 获取指定id的订单信息
     *
     * @param symbol  币对
     * @param orderId 订单id
     * @return 订单
     */
    Order getOrder(String symbol, String orderId);

    /**
     * 获取成交明细列表
     *
     * @param symbol  币对
     * @param orderId 订单id
     * @return
     */
    List<Trade> getTrade(String symbol, String orderId);

    /*****************期货********************/

    /**
     * 开多：买入开多
     *
     * @param instrument 合约
     * @param price      价格
     * @param quantity   数量
     * @param leverRate  杠杆
     * @return -1:下单失败
     */
    String openLong(String instrument,
                    BigDecimal price,
                    BigDecimal quantity,
                    Integer leverRate);

    /**
     * 开空：卖出开空
     *
     * @param instrument 合约
     * @param price      价格
     * @param quantity   数量
     * @param leverRate  杠杆
     * @return -1:下单失败
     */
    String openShort(String instrument,
                     BigDecimal price,
                     BigDecimal quantity,
                     Integer leverRate);

    /**
     * 平多：卖出平多
     *
     * @param instrument 合约
     * @param price      价格
     * @param quantity   数量
     * @param leverRate  杠杆
     * @return -1:下单失败
     */
    String closeLong(String instrument,
                     BigDecimal price,
                     BigDecimal quantity,
                     Integer leverRate);

    /**
     * 平空：买入平空
     *
     * @param instrument 合约
     * @param price      价格
     * @param quantity   数量
     * @param leverRate  杠杆
     * @return -1:下单失败
     */
    String closeShort(String instrument,
                      BigDecimal price,
                      BigDecimal quantity,
                      Integer leverRate);

    /**
     * 市价开多：买入开多
     *
     * @param instrument 合约
     * @param quantity   数量
     * @param leverRate  杠杆
     * @return -1:下单失败
     */
    String openLongMarket(String instrument,
                          BigDecimal quantity,
                          Integer leverRate);

    /**
     * 市价开空：卖出开空
     *
     * @param instrument 合约
     * @param quantity   数量
     * @param leverRate  杠杆
     * @return -1:下单失败
     */
    String openShortMarket(String instrument,
                           BigDecimal quantity,
                           Integer leverRate);

    /**
     * 市价平多：卖出平多
     *
     * @param instrument 合约
     * @param quantity   数量
     * @param leverRate  杠杆
     * @return -1:下单失败
     */
    String closeLongMarket(String instrument,
                           BigDecimal quantity,
                           Integer leverRate);

    /**
     * 市价平空：买入平空
     *
     * @param instrument 合约
     * @param quantity   数量
     * @param leverRate  杠杆
     * @return -1:下单失败
     */
    String closeShortMarket(String instrument,
                            BigDecimal quantity,
                            Integer leverRate);

    /**
     * 获取合约成交明细列表
     *
     * @param symbol 币对
     * @return List<Trade>
     */
    List<InstrumentTrade> getInstrumentTrade(String symbol);

    /**
     * 获取所有持仓
     *
     * @param instrument 合约
     * @return
     */
    List<Position> getPositions(String instrument);

    /**
     * 撤掉所有订单
     *
     * @param instrument 合约
     * @return
     */
    Boolean cancelAll(String instrument);

    /**
     * 委托下单：平买
     *
     * @param instrument   合约
     * @param triggerPrice 触发价，精度超过最小变动单位会报错
     * @param quantity     委托数量(张)
     * @param leverRate 杠杆
     * @return
     */
    String triggerCloseBuy(String instrument,
                           BigDecimal triggerPrice,
                           BigDecimal quantity,
                           Integer leverRate);

    /**
     * 委托下单：平卖
     *
     * @param instrument   合约
     * @param triggerPrice 触发价，精度超过最小变动单位会报错
     * @param quantity     委托数量(张)
     * @param leverRate 杠杆
     * @return
     */
    String triggerCloseSell(String instrument,
                            BigDecimal triggerPrice,
                            BigDecimal quantity,
                            Integer leverRate);


    /**
     * 获取计划委托所有的正在挂的订单
     *
     * @param instrument
     * @return
     */
    List<Order> getTriggerPendingOrders(String instrument);

    /**
     * 撤销所有计划委托订单
     *
     * @param instrument
     * @return
     */
    Boolean triggerCancelAll(String instrument);

    /**
     * 批量撤销计划订单
     * @param instrument 合约名
     * @param ids 订单id列表
     * @return
     */
    Boolean triggerCancelOrders(String instrument, List<String> ids);

    /**
     * 设置杠杆
     * @param instrument 合約名
     * @param leverage 杠杆
     * @return
     */
    Boolean setLeverage(String instrument, BigDecimal leverage);
}
