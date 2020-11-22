package com.zyf.trade.http.option;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.ITradeExchange;
import com.zyf.baseservice.util.deribit.DeribitTradeUtil;
import com.zyf.baseservice.util.huobipro.HuobiProOptionUtil;
import com.zyf.baseservice.util.huobipro.HuobiProSignUtil;
import com.zyf.common.model.*;
import com.zyf.common.model.enums.HttpMethod;
import com.zyf.common.model.enums.Side;
import com.zyf.common.model.http.HttpParameter;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 火币期权交易实现类
 * https://huobiapi.github.io/docs/option/v1/en/#query-order-detail
 * @author yuanfeng.z
 * @date 2020/9/19 11:57
 */
@Slf4j
public class HuobiProTradeOptionExchange implements ITradeExchange {
    /**
     * 协议
     */
    private static final String PROTOCOL = "https://";

    /**
     * 网站
     */
    private static final String SITE = "api.hbdm.com";

    /**
     * url
     */
    private static final String URL = PROTOCOL + SITE;

    /**
     * 公钥
     */
    private String accessKey = "";

    /**
     * 密钥
     */
    private String secretKey = "";

    /**
     * 期权订单 资源路径
     */
    private static final String OPTION_HIS_ORDERS = "/option-api/v1/option_hisorders";

    /**
     * 期权成交明细 资源路径
     */
    private static final String OPTION_TRADES = "/option-api/v1/option_order_detail";

    public HuobiProTradeOptionExchange(String accessKey, String secretKey) {
        super();
        this.accessKey = accessKey;
        this.secretKey = secretKey;
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
        String asset = symbol.split("-")[0];
        List<Order> orders = new ArrayList<>();
        int beginSize = orders.size();
        for (int i = 1; i <= 50; i++) {
            // 部分成交订单
            LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
            String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                    PROTOCOL,
                    SITE,
                    OPTION_HIS_ORDERS,
                    this.accessKey,
                    this.secretKey,
                    paramMap);
            Map<String, Object> map = new HashMap<>();
            map.put("symbol", asset);
//            map.put("contract_code", symbol);
            map.put("trade_type", 0);
            map.put("type", 1);
            map.put("status", 5);
            map.put("page_index", i);
            map.put("page_size", 50);
            map.put("create_date", 90);
            String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
            JSONObject jo = JSONObject.parseObject(jsonStr);
            orders.addAll(HuobiProOptionUtil.parseOrders(jo));

            // 全部成交订单
            Map<String, Object> map2 = new HashMap<>();
            map2.put("symbol", asset);
//            map2.put("contract_code", symbol);
            map2.put("trade_type", 0);
            map2.put("type", 1);
            map2.put("status", 6);
            map2.put("page_index", i);
            map2.put("page_size", 50);
            map2.put("create_date", 90);
            String jsonStr2 = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map2));
            JSONObject jo2 = JSONObject.parseObject(jsonStr2);
            orders.addAll(HuobiProOptionUtil.parseOrders(jo2));
            if (beginSize == orders.size()) {
                break;
            } else {
                beginSize = orders.size();
            }
        }
        List<Order> ordersWithoutDuplicates = orders.stream().distinct().collect(Collectors.toList());
        List<Order> newOrders = ordersWithoutDuplicates.stream().sorted(Comparator.comparing(Order::getTimestamp))
                .collect(Collectors.toList());
           return newOrders;
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
    public List<Trade> getTrades(String symbol, String paramCom) {
        String[] params = paramCom.split("/");

        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                OPTION_TRADES,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("symbol", symbol);
        map.put("order_id", params[0]);
        map.put("created_at", params[1]);
        map.put("order_type", params[2]);

        String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
        JSONObject jo = JSONObject.parseObject(jsonStr);
        return HuobiProOptionUtil.parseTrades(jo);
    }

    @Override
    public String openLong(String instrument, BigDecimal price, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public String openShort(String instrument, BigDecimal price, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public String closeLong(String instrument, BigDecimal price, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public String closeShort(String instrument, BigDecimal price, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public String openLongMarket(String instrument, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public String openShortMarket(String instrument, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public String closeLongMarket(String instrument, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public String closeShortMarket(String instrument, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public List<InstrumentTrade> getInstrumentTrade(String symbol) {
        return null;
    }

    @Override
    public List<Position> getPositions(String instrument) {
        return null;
    }

    @Override
    public Boolean cancelAll(String instrument) {
        return null;
    }

    @Override
    public String triggerCloseLong(String instrument, BigDecimal triggerPrice, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public String trackingCloseShort(String instrument, BigDecimal triggerPrice, BigDecimal quantity, BigDecimal callbackRate) {
        return null;
    }

    @Override
    public String trackingCloseLong(String instrument, BigDecimal triggerPrice, BigDecimal quantity, BigDecimal callbackRate) {
        return null;
    }

    @Override
    public String triggerCloseShort(String instrument, BigDecimal triggerPrice, BigDecimal quantity, Integer leverRate) {
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

    @Override
    public Boolean triggerCancelOrders(String instrument, List<String> ids) {
        return null;
    }

    @Override
    public Boolean setLeverage(String instrument, BigDecimal leverage) {
        return null;
    }
}
