package com.zyf.baseservice.baseexchange;

import com.zyf.baseservice.IExchange;
import com.zyf.common.model.*;
import com.zyf.common.model.enums.Side;
import com.zyf.common.model.enums.Type;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * Deribit交易所基类
 *
 * @author yuanfeng.z
 * @date 2019/10/5 10:12
 */
public class BaseDeribitExchange implements IExchange {
    @Override
    public Ticker getTicker(String symbol) {
        return null;
    }

    @Override
    public List<Kline> getKline(String symbol) {
        return null;
    }

    @Override
    public Depth getDepth(String symbol) {
        return null;
    }

    @Override
    public Map<String, Precision> getPrecisions() {
        return null;
    }

    @Override
    public Account getAccount(String symbol) {
        return null;
    }

    @Override
    public Asset getAsset() {
        return null;
    }

    @Override
    public String buyMarket(String symbol, BigDecimal cost) {
        return null;
    }

    @Override
    public String buyLimit(String symbol, BigDecimal price, BigDecimal quantity) {
        return null;
    }

    @Override
    public String buyLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage) {
        return null;
    }

    @Override
    public String buyLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage, Side side) {
        return null;
    }

    @Override
    public String buyStopMarket(String symbol, BigDecimal price, BigDecimal quantity) {
        return null;
    }

    @Override
    public String sellMarket(String symbol, BigDecimal quantity) {
        return null;
    }

    @Override
    public String sellLimit(String symbol, BigDecimal price, BigDecimal quantity) {
        return null;
    }

    @Override
    public String sellLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage) {
        return null;
    }

    @Override
    public String sellLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage, Side side) {
        return null;
    }

    @Override
    public String sellStopMarket(String symbol, BigDecimal price, BigDecimal quantity) {
        return null;
    }

    @Override
    public Boolean cancelOrder(String symbol, String orderId) {
        return null;
    }

    @Override
    public Boolean cancelOrders(String symbol, List<String> ids) {
        return null;
    }

    @Override
    public List<Order> getOrders(String symbol) {
        return null;
    }

    @Override
    public List<Order> getPendingOrders(String symbol) {
        return null;
    }

    @Override
    public Order getOrder(String symbol, String orderId) {
        return null;
    }

    @Override
    public List<Trade> getTrade(String symbol, String orderId) {
        return null;
    }

    @Override
    public List<InstrumentTrade> getInstrumentTrade(String symbol) {
        return null;
    }

    @Override
    public BigDecimal getContract(String symbol) {
        return null;
    }

    @Override
    public List<Order> get_unsettled_orders(String symbol) {
        return null;
    }

    @Override
    public Boolean cancelAll(String symbol) {
        return null;
    }

    @Override
    public Boolean cancelAll() {
        return null;
    }

    @Override
    public Boolean placeOrders(String symbol, List<Order> orders, Integer leverage) {
        return null;
    }

    @Override
    public String closePosition(String symbol, Type type, BigDecimal price) {
        return null;
    }

    @Override
    public List<Order> getPosition(String symbol) {
        return null;
    }
}
