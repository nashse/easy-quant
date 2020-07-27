package com.zyf.baseservice.baseexchange;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.IExchange;
import com.zyf.baseservice.util.okex.OkexUtil;
import com.zyf.common.model.*;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.model.enums.Side;
import com.zyf.common.model.enums.Type;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * okex交易所父类
 *
 * @author yuanfeng.z
 * @date 2019/6/29 10.21
 */
public class BaseOkexExchange implements IExchange {

    /**
     * 协议
     */
    protected static final String PROTOCOL = "https://";

    /**
     * 网站
     */
    protected static final String SITE = "www.okex.com";

    /**
     * url
     */
    protected static final String URL = PROTOCOL +SITE;

    /**
     * get请求方法
     */
    protected static final String METHOD_GET = "GET";

    /**
     * 价格、数量精度资源路径
     */
    private static final String PRECISION = "/api/spot/v3/instruments";

    /*********对象属性***********/

    /**
     * 交易所名字
     */
    private final ExchangeEnum name = ExchangeEnum.OKEXV3;


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

    /**
     * 获取币对精度
     * @return 币对精度列表
     */
    @Override
    public Map<String, Precision> getPrecisions() {
        /*获取币种精度数据*/
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(PRECISION);
        String str = OkHttpV3ClientProxy.get(sb.toString());

        /*适配*/
        JSONArray jo = JSONObject.parseArray(str);
        Map<String, Precision> map = OkexUtil.parsePrecisions(jo);
        return map;
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
    public  List<Trade> getTrade(String symbol, String orderId) {
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
