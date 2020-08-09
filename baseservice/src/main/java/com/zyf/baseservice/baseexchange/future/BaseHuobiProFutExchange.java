package com.zyf.baseservice.baseexchange.future;

import com.zyf.baseservice.IExchange;
import com.zyf.common.model.*;
import com.zyf.common.model.enums.Side;
import com.zyf.common.model.enums.Type;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 火币期货基类
 * @author yuanfeng.z
 * @date 2020/7/29 18:41
 */
public class BaseHuobiProFutExchange implements IExchange {

    /**
     * 协议
     */
    protected static final String PROTOCOL = "https://";

    /**
     * 网站
     */
    protected static final String SITE = "api.hbdm.com";

    /**
     * url
     */
    protected static final String URL = PROTOCOL +SITE;

    /**
     * 下单资源路径
     */
    protected static final String ORDER = "/swap-api/v1/swap_order";

    /**
     * 持仓
     */
    protected static final String POSITION = "/swap-api/v1/swap_position_info";

    /**
     * 获取合约当前未成交委托
     */
    protected static final String PENDING_ORDERS = "/swap-api/v1/swap_openorders";

    /**
     * 获取账户
     */
    protected static final String ACCOUNT = "/swap-api/v1/swap_account_info";

    /**
     * 撤销所有订单
     */
    protected static final String CANCEL_ALL = "/swap-api/v1/swap_cancelall";

    /**
     * 合约计划委托下单
     */
    protected static final String TRIGGER_ORDER = "/swap-api/v1/swap_trigger_order";

    /**
     * 获取计划委托当前委托
     */
    protected static final String TRIGGER_PENDING_ORDERS = "/swap-api/v1/swap_trigger_openorders";

    /**
     * 合约计划委托全部撤单
     */
    protected static final String TRIGGER_CANCEL_ALL = "/swap-api/v1/swap_trigger_cancelall";


    public BaseHuobiProFutExchange() {

    }

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
    public String openBuy(String instrument, BigDecimal price, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public String openSell(String instrument, BigDecimal price, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public String closeBuy(String instrument, BigDecimal price, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public String closeSell(String instrument, BigDecimal price, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public List<InstrumentTrade> getInstrumentTrade(String symbol) {
        return null;
    }

    @Override
    public List<Order> getPosition(String symbol) {
        return null;
    }

    @Override
    public Boolean cancelAll(String instrument) {
        return null;
    }

    @Override
    public String triggerCloseBuy(String instrument, BigDecimal triggerPrice, BigDecimal quantity) {
        return null;
    }

    @Override
    public String triggerCloseSell(String instrument, BigDecimal triggerPrice, BigDecimal quantity) {
        return null;
    }

    @Override
    public List<Order> getTriggerPendingOrders(String instrument) {
        return null;
    }

    @Override
    public Boolean triggerCancelAll(String instrument) {
        return null;
    }

}
