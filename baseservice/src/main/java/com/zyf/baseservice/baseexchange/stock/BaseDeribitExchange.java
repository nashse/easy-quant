//package com.zyf.baseservice.baseexchange.stock;
//
//import com.zyf.baseservice.IExchange;
//import com.zyf.baseservice.IMdExchange;
//import com.zyf.common.model.*;
//import com.zyf.common.model.enums.ExchangeEnum;
//import com.zyf.common.model.enums.Side;
//import com.zyf.common.model.enums.Type;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//
///**
// * Deribit交易所基类
// *
// * @author yuanfeng.z
// * @date 2019/10/5 10:12
// */
//public class BaseDeribitExchange implements IMdExchange {
//
//    /**
//     * 协议
//     */
//    protected static final String PROTOCOL = "https://";
//
//    /**
//     * 网站
//     */
//    protected static final String SITE = "www.deribit.com";
//
//    /**
//     * URL
//     */
//    protected static final String URL = PROTOCOL + SITE;
//
//    /**
//     * ticker资源路径
//     */
//    protected static final String TICKEER = "/api/v2/public/ticker";
//
//    /**
//     * depth资源路径
//     */
//    protected static final String DEPTH = "/api/v2/public/get_order_book";
//
//    /**
//     * 精密度
//     */
//    protected static final String PRECISION = "/api/v2/api/v1/instrument";
//
//    /**
//     * 合约
//     */
//    protected static final String INSTRUMENTS = "/api/v2/public/get_instruments";
//
//    /**
//     * 币对
//     */
//    protected static final String CURRENCIES = "/api/v2/public/get_currencies";
//
//    /**
//     * 交易所名字
//     */
//    protected static final ExchangeEnum name = ExchangeEnum.DERIBIT;
//
//    @Override
//    public Ticker getTicker(String symbol) {
//        return null;
//    }
//
//    @Override
//    public List<Kline> getKline(String symbol) {
//        return null;
//    }
//
//    @Override
//    public Depth getDepth(String symbol) {
//        return null;
//    }
//
//    @Override
//    public Map<String, Precision> getPrecisions() {
//        return null;
//    }
//
//    @Override
//    public Account getAccount(String symbol) {
//        return null;
//    }
//
//    @Override
//    public Asset getAsset() {
//        return null;
//    }
//
//    @Override
//    public String buyMarket(String symbol, BigDecimal cost) {
//        return null;
//    }
//
//    @Override
//    public String buyLimit(String symbol, BigDecimal price, BigDecimal quantity) {
//        return null;
//    }
//
//    @Override
//    public String buyLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage) {
//        return null;
//    }
//
//    @Override
//    public String buyLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage, Side side) {
//        return null;
//    }
//
//    @Override
//    public String buyStopMarket(String symbol, BigDecimal price, BigDecimal quantity) {
//        return null;
//    }
//
//    @Override
//    public String sellMarket(String symbol, BigDecimal quantity) {
//        return null;
//    }
//
//    @Override
//    public String sellLimit(String symbol, BigDecimal price, BigDecimal quantity) {
//        return null;
//    }
//
//    @Override
//    public String sellLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage) {
//        return null;
//    }
//
//    @Override
//    public String sellLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage, Side side) {
//        return null;
//    }
//
//    @Override
//    public String sellStopMarket(String symbol, BigDecimal price, BigDecimal quantity) {
//        return null;
//    }
//
//    @Override
//    public Boolean cancelOrder(String symbol, String orderId) {
//        return null;
//    }
//
//    @Override
//    public Boolean cancelOrders(String symbol, List<String> ids) {
//        return null;
//    }
//
//    @Override
//    public List<Order> getOrders(String symbol) {
//        return null;
//    }
//
//    @Override
//    public List<Order> getPendingOrders(String symbol) {
//        return null;
//    }
//
//    @Override
//    public Order getOrder(String symbol, String orderId) {
//        return null;
//    }
//
//    @Override
//    public List<Trade> getTrade(String symbol, String orderId) {
//        return null;
//    }
//
//    @Override
//    public String openBuy(String instrument, BigDecimal price, BigDecimal quantity, Integer leverRate) {
//        return null;
//    }
//
//    @Override
//    public String openSell(String instrument, BigDecimal price, BigDecimal quantity, Integer leverRate) {
//        return null;
//    }
//
//    @Override
//    public String closeBuy(String instrument, BigDecimal price, BigDecimal quantity, Integer leverRate) {
//        return null;
//    }
//
//    @Override
//    public String closeSell(String instrument, BigDecimal price, BigDecimal quantity, Integer leverRate) {
//        return null;
//    }
//
//    @Override
//    public List<InstrumentTrade> getInstrumentTrade(String symbol) {
//        return null;
//    }
//
//    @Override
//    public List<Order> getPosition(String symbol) {
//        return null;
//    }
//
//    @Override
//    public Boolean cancelAll(String instrument) {
//        return null;
//    }
//
//    @Override
//    public String triggerCloseBuy(String instrument, BigDecimal triggerPrice, BigDecimal quantity) {
//        return null;
//    }
//
//    @Override
//    public String triggerCloseSell(String instrument, BigDecimal triggerPrice, BigDecimal quantity) {
//        return null;
//    }
//
//    @Override
//    public List<Order> getTriggerPendingOrders(String instrument) {
//        return null;
//    }
//
//    @Override
//    public Boolean triggerCancelAll(String instrument) {
//        return null;
//    }
//}
