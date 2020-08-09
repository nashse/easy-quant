package com.zyf.trade.proxy;

import com.zyf.baseservice.ITradeExchange;
import com.zyf.common.model.*;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.model.enums.SecuritiesTypeEnum;
import com.zyf.common.util.CommomUtil;
import com.zyf.trade.factory.TradeExchangeFactory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 交易代理类（统一异常处理）
 * @author yuanfeng.z
 * @date 2019/7/4 11:26
 */
@Getter
@ToString
@EqualsAndHashCode
public class TradeExchangeProxy {
    /**
     * 交易交易所对象
     */
    private ITradeExchange tradeExchange;

    /**
     * 交易所名称
     */
    private ExchangeEnum exchangeNameEnum;

    /**
     * 交易所名称
     */
    private String exchangeName;

    /**
     * 所有币种精度
     */
    Map<String, Precision> precisionMap;

    public TradeExchangeProxy(ExchangeEnum exchangeName, SecuritiesTypeEnum type, String accessKey, String secretKey) {
        this.tradeExchange = TradeExchangeFactory.createTradeExchange(exchangeName, type, accessKey, secretKey);
        this.exchangeNameEnum = exchangeName;
        this.exchangeName = exchangeName.getValue();
    }

    public TradeExchangeProxy(String exchangeName, String type, String accessKey, String secretKey) {
        this(CommomUtil.toExchangeEnum(exchangeName), CommomUtil.toSecuritiesTypeEnum(type), accessKey, secretKey);
    }

    public ITradeExchange getE() {
        return this.tradeExchange;
    }

    /**
     * 获取账户信息
     * @param symbol
     * @return
     */
    public Account getAccount(String symbol) {
        Account account = this.tradeExchange.getAccount(symbol);
        return account;
    }

    /**
     * 市价买入
     * @param symbol 币对
     * @param cost 费用
     * @return 订单id
     */
    public String buyMarket(String symbol, BigDecimal cost) {
        String id = this.tradeExchange.buyMarket(symbol, cost);
        return id;
    }

    /**
     * 市价卖出
     * @param symbol 币对
     * @param quantity 卖出数量
     * @return 订单id
     */
    public String sellMarket(String symbol, BigDecimal quantity) {
        String id = this.tradeExchange.sellMarket(symbol, quantity);
        return id;
    }

    /**
     * 限价买入
     * @param symbol 币对
     * @param price 价格
     * @param quanatity 价格
     * @return 订单id
     */
    public String buyLimit(String symbol, BigDecimal price, BigDecimal quanatity) {
        String id = this.tradeExchange.buyLimit(symbol, price, quanatity);
        return id;
    }

    /**
     * 限价卖出
     * @param symbol 币对
     * @param price 价格
     * @param quanatity 价格
     * @return 订单id
     */
    public String sellLimit(String symbol, BigDecimal price, BigDecimal quanatity) {
        String id = this.tradeExchange.sellLimit(symbol, price, quanatity);
        return id;
    }


    /**
     * 根据订单id撤单
     * @param symbol 币对
     * @param orderId 订单id
     * @return
     */
    public Boolean cancelOrder(String symbol, String orderId) {
        Boolean b = this.tradeExchange.cancelOrder(symbol, orderId);
        return b;
    }

    /**
     * 批量撤单
     * @param symbol 币对
     * @param ids
     * @return
     */
    public Boolean cancelOrders(String symbol, List<String> ids) {
        Boolean b = this.tradeExchange.cancelOrders(symbol, ids);
        return b;
    }

    /**
     * 获取所有的订单
     * @param symbol 币对
     * @return 订单列表
     */
    public List<Order> getOrders(String symbol) {
        List<Order> orders = this.tradeExchange.getOrders(symbol);
        return orders;
    }

    /**
     * 获取指定id的订单信息
     * @param symbol 币对
     * @param orderId 订单id
     * @return 订单
     */
    public Order getOrder(String symbol, String orderId) {
        Order orders = this.tradeExchange.getOrder(symbol, orderId);
        return orders;
    }

    /**
     * 获取成交明细
     * @param symbol
     * @param orderId
     * @return
     */
    public List<Trade> getTrade(String symbol, String orderId) {
        List<Trade> trades = this.tradeExchange.getTrade(symbol, orderId);
        return trades;
    }

    /**
     * 获取挂单
     * @param symbol
     * @return
     */
    public List<Order> getPendingOrders(String symbol) {
        List<Order> pendingOrders = this.tradeExchange.getPendingOrders(symbol);
        return pendingOrders;
    }

    /**
     * 获取所有持仓
     * @param symbol
     * @return
     */
    public List<Position> getPosition(String symbol) {
        List<Position> positions = this.tradeExchange.getPositions(symbol);
        return positions;
    }
}
