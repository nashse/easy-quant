package com.zyf.trade.http.future;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.ITradeExchange;
import com.zyf.baseservice.util.huobipro.HuobiProSignUtil;
import com.zyf.baseservice.util.huobipro.HuobiProFutUtil;
import com.zyf.common.model.*;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.model.enums.HttpMethod;
import com.zyf.common.model.enums.Offset;
import com.zyf.common.model.enums.Side;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;
import javafx.geometry.Pos;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 火币期货交易类
 *
 * @author yuanfeng.z
 * @date 2020/7/29 20:10
 */
public class HuobiProTradeFutExchange implements ITradeExchange {

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
     * 交易所名字
     */
    protected final ExchangeEnum name = ExchangeEnum.HUOBIPRO;

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

    /**
     * 公钥
     */
    private String accessKey = "";

    /**
     * 密钥
     */
    private String secretKey = "";

    /**
     * 账户id
     */
    private String accountId = "";

    public HuobiProTradeFutExchange(String accessKey, String secretKey) {
        super();
        this.accessKey = accessKey;
        this.secretKey = secretKey;
    }

    @Override
    public Map<String, Precision> getPrecisions() {
        return null;
    }

    /**
     * 获取账户信息
     *
     * @param instrument
     * @return
     */
    @Override
    public Account getAccount(String instrument) {
        // 转换币对格式
        String ins = HuobiProFutUtil.transfSymbol(instrument);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                ACCOUNT,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("contractCode", ins);

        String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
        JSONObject json = JSON.parseObject(jsonStr);
        Account account = HuobiProFutUtil.parseAccount(instrument, json);

        return account;
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

    /**
     * 撤掉所有订单
     * @param instrument
     * @return
     */
    @Override
    public Boolean cancelAll(String instrument) {
        // 转换币对格式
        instrument = HuobiProFutUtil.transfSymbol(instrument);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                CANCEL_ALL,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("contract_code", instrument);

        String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
        JSONObject json = JSON.parseObject(jsonStr);
        Boolean b = HuobiProFutUtil.parseCancelAll(json);

        return b;
    }

    /**
     * 获取合约当前未成交委托
     *
     * @param instrument 合约名
     * @return
     */
    @Override
    public List<Order> getPendingOrders(String instrument) {
        // 转换币对格式
        instrument = HuobiProFutUtil.transfSymbol(instrument);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                PENDING_ORDERS,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("contract_code", instrument);
        map.put("page_index", 1);
        map.put("page_size", 50);

        String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
        JSONObject json = JSON.parseObject(jsonStr);
        List<Order> orders = HuobiProFutUtil.parsePendingOrders(json);

        return orders;
    }

    @Override
    public Order getOrder(String symbol, String orderId) {
        return null;
    }

    @Override
    public List<Trade> getTrade(String symbol, String orderId) {
        return null;
    }

    /**
     * 获取持仓
     *
     * @param instrument 合约名
     * @return
     */
    @Override
    public List<Position> getPositions(String instrument) {
        // 转换币对格式
        instrument = HuobiProFutUtil.transfSymbol(instrument);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                POSITION,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("contractCode", instrument);

        String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
        JSONObject json = JSON.parseObject(jsonStr);
        List<Position> positions = HuobiProFutUtil.parsePosition(json);

        return positions;
    }

    /**
     * 开多：买入开多
     *
     * @param instrument 合约
     * @param price      价格
     * @param quantity   数量
     * @param leverRate  杠杆
     * @return
     */
    @Override
    public String openLong(String instrument,
                           BigDecimal price,
                           BigDecimal quantity,
                           Integer leverRate) {
        // 转换币对格式
        instrument = HuobiProFutUtil.transfSymbol(instrument);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                ORDER,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("contractCode", instrument);
        map.put("client_order_id", "");
        map.put("price", price);
        map.put("volume", quantity);
        map.put("direction", Side.BUY.getValue());
        map.put("offset", Offset.OPEN.getValue());
        map.put("lever_rate", leverRate);
        map.put("order_price_type", "limit");

        String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
        JSONObject json = JSON.parseObject(jsonStr);
        String orderId = null;
        final String ok = "ok";
        if (ok.equals(json.getString("status"))) {
            orderId = json.getString("data");
        }

        return orderId;
    }

    /**
     * 开空：卖出开空
     *
     * @param instrument 合约
     * @param price      价格
     * @param quantity   数量
     * @param leverRate  杠杆
     * @return
     */
    @Override
    public String openShort(String instrument,
                            BigDecimal price,
                            BigDecimal quantity,
                            Integer leverRate) {
        // 转换币对格式
        instrument = HuobiProFutUtil.transfSymbol(instrument);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                ORDER,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("contractCode", instrument);
        map.put("client_order_id", "");
        map.put("price", price);
        map.put("volume", quantity);
        map.put("direction", Side.SELL.getValue());
        map.put("offset", Offset.OPEN.getValue());
        map.put("lever_rate", leverRate);
        map.put("order_price_type", "limit");

        String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
        JSONObject json = JSON.parseObject(jsonStr);
        String orderId = null;
        final String ok = "ok";
        if (ok.equals(json.getString("status"))) {
            orderId = json.getString("data");
        }

        return orderId;
    }

    /**
     * 平多：卖出平多
     *
     * @param instrument 合约
     * @param price      价格
     * @param quantity   数量
     * @param leverRate  杠杆
     * @return
     */
    @Override
    public String closeLong(String instrument,
                            BigDecimal price,
                            BigDecimal quantity,
                            Integer leverRate) {
        // 转换币对格式
        instrument = HuobiProFutUtil.transfSymbol(instrument);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                ORDER,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("contractCode", instrument);
        map.put("client_order_id", "");
        map.put("price", price);
        map.put("volume", quantity);
        map.put("direction", Side.BUY.getValue());
        map.put("offset", Offset.CLOSE.getValue());
        map.put("lever_rate", leverRate);
        map.put("order_price_type", "limit");

        String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
        JSONObject json = JSON.parseObject(jsonStr);
        String orderId = null;
        final String ok = "ok";
        if (ok.equals(json.getString("status"))) {
            orderId = json.getString("data");
        }

        return orderId;
    }

    /**
     * 平空：买入平空
     *
     * @param instrument 合约
     * @param price      价格
     * @param quantity   数量
     * @param leverRate  杠杆
     * @return
     */
    @Override
    public String closeShort(String instrument,
                             BigDecimal price,
                             BigDecimal quantity,
                             Integer leverRate) {
        // 转换币对格式
        instrument = HuobiProFutUtil.transfSymbol(instrument);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                ORDER,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("contractCode", instrument);
        map.put("client_order_id", "");
        map.put("price", price);
        map.put("volume", quantity);
        map.put("direction", Side.SELL.getValue());
        map.put("offset", Offset.CLOSE.getValue());
        map.put("lever_rate", leverRate);
        map.put("order_price_type", "limit");

        String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
        JSONObject json = JSON.parseObject(jsonStr);
        String orderId = null;
        final String ok = "ok";
        if (ok.equals(json.getString("status"))) {
            orderId = json.getString("data");
        }

        return orderId;
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

    /**
     * 委托下单：平买
     * @param instrument 合约
     * @param triggerPrice 触发价，精度超过最小变动单位会报错
     * @param quantity 委托数量(张)
     * @param leverRate 杠杆
     * @return
     */
    @Override
    public String triggerCloseBuy(String instrument,
                                  BigDecimal triggerPrice,
                                  BigDecimal quantity,
                                  Integer leverRate) {
        // 转换币对格式
        instrument = HuobiProFutUtil.transfSymbol(instrument);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                TRIGGER_ORDER,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("contract_code", instrument);
        map.put("trigger_type", "le");
        map.put("trigger_price", triggerPrice);
        map.put("order_price", triggerPrice);
        map.put("order_price_type", "optimal_10");
        map.put("volume", quantity);
        map.put("direction", Side.BUY.getValue());
        map.put("offset", Offset.CLOSE.getValue());
        map.put("lever_rate", leverRate);

        String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
        JSONObject json = JSON.parseObject(jsonStr);
        String orderId = null;
        final String ok = "ok";
        if (ok.equals(json.getString("status"))) {
            orderId = json.getJSONObject("data").getString("order_id");
        }

        return orderId;
    }

    /**
     * 委托下单：平卖
     * @param instrument 合约
     * @param triggerPrice 触发价，精度超过最小变动单位会报错
     * @param quantity 委托数量(张)
     * @param leverRate 杠杆
     * @return
     */
    @Override
    public String triggerCloseSell(String instrument,
                                   BigDecimal triggerPrice,
                                   BigDecimal quantity,
                                   Integer leverRate) {
        // 转换币对格式
        instrument = HuobiProFutUtil.transfSymbol(instrument);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                TRIGGER_ORDER,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("contract_code", instrument);
        map.put("trigger_type", "ge");
        map.put("trigger_price", triggerPrice);
        map.put("order_price", triggerPrice);
        map.put("order_price_type", "optimal_10");
        map.put("volume", quantity);
        map.put("direction", Side.SELL.getValue());
        map.put("offset", Offset.CLOSE.getValue());

        String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
        JSONObject json = JSON.parseObject(jsonStr);
        String orderId = null;
        final String ok = "ok";
        if (ok.equals(json.getString("status"))) {
            orderId = json.getJSONObject("data").getString("order_id");
        }

        return orderId;
    }

    /**
     * 获取计划委托所有的正在挂的订单
     *
     * @param instrument
     * @return
     */
    @Override
    public List<Order> getTriggerPendingOrders(String instrument) {
        // 转换币对格式
        instrument = HuobiProFutUtil.transfSymbol(instrument);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                TRIGGER_PENDING_ORDERS,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("contract_code", instrument);
        map.put("page_index", 1);
        map.put("page_size", 50);

        String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
        JSONObject json = JSON.parseObject(jsonStr);
        List<Order> orders = HuobiProFutUtil.parsePendingOrders(json);

        return orders;
    }

    /**
     * 撤销所有计划委托订单
     *
     * @param instrument
     * @return
     */
    @Override
    public Boolean triggerCancelAll(String instrument) {
        // 转换币对格式
        instrument = HuobiProFutUtil.transfSymbol(instrument);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                TRIGGER_CANCEL_ALL,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("contract_code", instrument);

        String jsonStr = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));
        JSONObject json = JSON.parseObject(jsonStr);
        Boolean b = HuobiProFutUtil.parseCancelAll(json);

        return b;
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
