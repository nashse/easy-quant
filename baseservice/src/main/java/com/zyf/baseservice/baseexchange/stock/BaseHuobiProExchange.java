//package com.zyf.baseservice.baseexchange.stock;
//
//import com.alibaba.fastjson.JSONObject;
//import com.zyf.baseservice.IExchange;
//import com.zyf.baseservice.IMdExchange;
//import com.zyf.baseservice.util.huobipro.HuobiProUtil;
//import com.zyf.common.model.*;
//import com.zyf.common.model.enums.ExchangeEnum;
//import com.zyf.common.model.enums.Side;
//import com.zyf.common.model.enums.Type;
//import com.zyf.common.okhttp.OkHttpV3ClientProxy;
//
//import java.math.BigDecimal;
//import java.util.List;
//import java.util.Map;
//
///**
// * 火币pro交易所父类
// *
// * @author yuanfeng.z
// * @date 2019/6/28 15:31
// */
//public class BaseHuobiProExchange implements IMdExchange {
//    /**
//     * 协议
//     */
//    protected static final String PROTOCOL = "https://";
//
//    /**
//     * 网站
//     */
//    protected static final String SITE = "api.huobi.pro";
//
//    /**
//     * url
//     */
//    protected static final String URL = PROTOCOL +SITE;
//
//    /**
//     * 价格、数量精度资源路径
//     */
//    protected static final String PRESISION = "/v1/common/symbols";
//
//    /*********对象属性***********/
//
//    /**
//     * 交易所名字
//     */
//    protected final ExchangeEnum name = ExchangeEnum.HUOBIPRO;
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
//    /**
//     * 获取币对精度
//     * @return 币对精度列表
//     */
//    @Override
//    public Map<String, Precision> getPrecisions() {
//        /*获取币种精度数据*/
//        StringBuilder sb = new StringBuilder();
//        sb.append(URL).append(PRESISION);
//        String accuracysStr = OkHttpV3ClientProxy.get(sb.toString());
//
//        /*适配*/
//        JSONObject jo = JSONObject.parseObject(accuracysStr);
//        Map<String, Precision> precisions = HuobiProUtil.parsePrecisions(jo);
//        return precisions;
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
//    public  List<Trade> getTrade(String symbol, String orderId) {
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
