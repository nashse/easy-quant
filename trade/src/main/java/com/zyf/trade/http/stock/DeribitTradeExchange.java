package com.zyf.trade.http.stock;

import cn.hutool.core.util.RandomUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.ITradeExchange;
import com.zyf.baseservice.util.deribit.DeribitTradeUtil;
import com.zyf.common.crypto.HmacUtil;
import com.zyf.common.model.*;
import com.zyf.common.model.enums.Side;
import com.zyf.common.model.http.HttpParameter;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;
import javafx.geometry.Pos;
import lombok.extern.slf4j.Slf4j;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Deribit现货交易实现类
 *
 * @author yuanfeng.z
 * @date 2019/10/5 11:51
 */
@Slf4j
public class DeribitTradeExchange implements ITradeExchange {
    /**
     * 协议
     */
    public static final String PROTOCOL = "https://";
    /**
     * 网站
     */
    public static final String SITE = "test.deribit.com";
    /**
     * URL
     */
    public static final String URL = PROTOCOL + SITE;

    public static final String ACCOUNT = "/api/v2/private/get_account_summary";
    public static final String ORDERS_currency = "/api/v2/private/get_open_orders_by_currency";
    public static final String ORDERS_instrument = "/api/v2/private/get_open_orders_by_instrument";
    public static final String ORDER = "/api/v2/private/get_order_state";
    public static final String CANCEL = "/api/v2/private/cancel";
    public static final String CANCEL_ALL = "/api/v2/private/cancel_all";
    public static final String CANCEL_ALL_BY_SYMBOL = "/api/v2/private/cancel_all_by_instrument";
    public static final String CANCEL_ALL_BY_CURRENCY = "/api/v2/private/cancel_all_by_currency";
    public static final String ORDER_BUY = "/api/v2/private/buy";
    public static final String ORDER_SELL = "/api/v2/private/sell";
    public static final String HISTORY_currency = "/api/v2/private/get_order_history_by_currency";
    public static final String HISTORY_instrument = "/api/v2/private/get_order_history_by_instrument";
    public static final String TRADE_BY_INSTRUMENT = "/api/v2/private/get_user_trades_by_instrument";

    public static final String OPEN_ORDERS = "/api/v2/private/get_open_orders_by_instrument";

    public static final String CLOSE_POSITION = "/api/v2/private/close_position";

    /**
     * 仓位
     */
    public static final String CONTRACT = "/api/v1/private/positions";

    public static final String AUTH = "/api/v2/public/auth";
    private String ACCESS_KEY;
    private String ACCESS_SECRET;

    public Integer tokenType;

    public enum OrderType {
        LIMIT,
        MARKET,
        STOP_MARKET,
        STOP_LIMIT
    }

    public enum BuySellType {
        BUY,
        SELL
    }

    public DeribitTradeExchange(String accessKey, String secretKey) {
        this.ACCESS_KEY = accessKey;
        this.ACCESS_SECRET = secretKey;
        this.tokenType = 0;
    }


    /**
     * @param currency
     * @return
     */
    public Balance getBalance(String currency) {
        HttpParameter httpParameter = HttpParameter.build();
        httpParameter.add("currency", currency.toUpperCase());
        httpParameter.add("extended", "true");
        String url = URL + ACCOUNT + "?" + httpParameter.concat();
        String jsonStr = OkHttpV3ClientProxy.get(url, "Authorization", getToken());
        JSONObject jo = JSONObject.parseObject(jsonStr);
        return DeribitTradeUtil.parseBalance(jo);
    }

    /**
     * 此方法取消所有用户的订单，并停止所有货币和仪器种类的订单。
     */
    public Boolean cancelAll() {
        HttpParameter httpParameter = HttpParameter.build();
        String url = URL + CANCEL_ALL + "?" + httpParameter.concat();
        String jsonStr = OkHttpV3ClientProxy.get(url, "Authorization", getToken());
        return JSONObject.parseObject(jsonStr).getInteger("result") > -1;
    }

    /**
     * 限价买入
     *
     * @param symbol 币对
     * @param amount 数量
     * @param price  价格
     * @return 订单id
     */
    public String buyOrSellLimitOrMarket(String symbol, BigDecimal amount, BigDecimal price, OrderType type, BuySellType buySellType) {
        // https://test.deribit.com/api/v2/private/buy?instrument_name=BTC-27DEC19-40000-C&amount=0.1&price=0.0015&type=limit
        HttpParameter parameter = HttpParameter.build();
        parameter.add("instrument_name", symbol.toUpperCase());
        parameter.add("amount", amount);
        String url = null;

        parameter.add("type", type.toString());

        if (type.toString().equals("limit")) {
            parameter.add("price", price);
            parameter.add("time_in_force", "good_til_cancelled");
        } else if (type.toString().equals("market")) {
        } else if (type.toString().equals("stop_market")) {
            parameter.add("stop_price", price);
            parameter.add("max_show", amount);
            parameter.add("trigger", "mark_price");
            parameter.add("time_in_force", "good_til_cancelled");
        }

        if (buySellType.toString().equals("BUY")) {
            url = URL + ORDER_BUY + "?" + parameter.concat();
        } else if (buySellType.toString().equals("SELL")) {
            url = URL + ORDER_SELL + "?" + parameter.concat();
        }

        String jsonStr = OkHttpV3ClientProxy.get(url, "Authorization", getToken());
        JSONObject jo = JSONObject.parseObject(jsonStr);
        if (Objects.nonNull(jo.getJSONObject("error"))) {
            JSONObject error = jo.getJSONObject("error");
            log.error(error.getString("message"));
            return null;
        }
        return DeribitTradeUtil.parsebuyOrSellLimitOrMarket(jo);
    }

    @Override
    public Map<String, Precision> getPrecisions() {
        return null;
    }

    @Override
    public Account getAccount(String symbol) {
        String[] symbols = symbol.split("/");

        Balance left = null;
        Balance right = null;
        ArrayList arrayList = new ArrayList();
        if (symbols.length >= 2) {
            left = getBalance(symbols[0]);
            right = getBalance(symbols[1]);
            arrayList.add(JSON.toJSON(left.getOriginData()));
            arrayList.add(JSON.toJSON(right.getOriginData()));
        } else if (symbols.length == 1) {
            left = getBalance(symbols[0]);
            arrayList.add(JSON.toJSON(left.getOriginData()));
        } else {
            return null;
        }

        return new Account(arrayList.toString(), symbol, left, right);
    }

    @Override
    public String buyLimit(String symbol, BigDecimal price, BigDecimal quantity) {
        return buyOrSellLimitOrMarket(symbol, quantity, price, OrderType.LIMIT, BuySellType.BUY);
    }

    @Override
    public String buyLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage) {
        return null;
    }

    @Override
    public String sellLimit(String symbol, BigDecimal price, BigDecimal quantity) {
        return buyOrSellLimitOrMarket(symbol, quantity, price, OrderType.LIMIT, BuySellType.SELL);
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
    public String buyMarket(String symbol, BigDecimal cost) {
        return buyOrSellLimitOrMarket(symbol, cost, null, OrderType.MARKET, BuySellType.BUY);
    }

    @Override
    public String sellMarket(String symbol, BigDecimal quantity) {
        return buyOrSellLimitOrMarket(symbol, quantity, null, OrderType.MARKET, BuySellType.SELL);
    }

    @Override
    public String buyLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer leverage, Side side) {
        return buyOrSellLimitOrMarket(symbol, quantity, price, OrderType.LIMIT, BuySellType.BUY);
    }

    @Override
    public String buyStopMarket(String symbol, BigDecimal price, BigDecimal quantity) {
        return buyOrSellLimitOrMarket(symbol, quantity, price, OrderType.STOP_MARKET, BuySellType.BUY);
    }

    @Override
    public String sellStopMarket(String symbol, BigDecimal price, BigDecimal quantity) {
        return buyOrSellLimitOrMarket(symbol, quantity, price, OrderType.STOP_MARKET, BuySellType.SELL);
    }

    @Override
    public Boolean cancelOrder(String symbol, String orderId) {
        HttpParameter parameter = HttpParameter.build();
        parameter.add("order_id", orderId);
        String url = URL + CANCEL + "?" + parameter.concat();
        String jsonStr = OkHttpV3ClientProxy.get(url, "Authorization", getToken());
        JSONObject jo = JSONObject.parseObject(jsonStr);
        if (Objects.nonNull(jo.getJSONObject("error"))) {
            JSONObject error = jo.getJSONObject("error");
            log.error(error.getString("message"));
            return false;
        }
        JSONObject result = jo.getJSONObject("result");
        if (Objects.nonNull(result)) {
            return true;
        }
        return false;
    }

    @Override
    public Boolean cancelOrders(String symbol, List<String> ids) {
        HttpParameter parameter = HttpParameter.build();
        parameter.add("instrument_name", symbol);
        parameter.add("type", "all");
        String url = URL + CANCEL_ALL_BY_SYMBOL + "?" + parameter.concat();
        String jsonStr = OkHttpV3ClientProxy.get(url, "Authorization", getToken());
        JSONObject jo = JSONObject.parseObject(jsonStr);
        return true;
    }


    @Override
    public List<Order> getOrders(String instrument_name) {
        HttpParameter parameter = HttpParameter.build();
        parameter.add("instrument_name", instrument_name);
        String url = URL + HISTORY_instrument + "?" + parameter.concat();
        String jsonStr = OkHttpV3ClientProxy.get(url, "Authorization", getToken());
        JSONObject jo = JSONObject.parseObject(jsonStr);
        return DeribitTradeUtil.parseOrders(jo);
    }

    @Override
    public List<Order> getPendingOrders(String instrument_name) {
        return getOrders_instrument(instrument_name);
    }

    @Override
    public Order getOrder(String symbol, String orderId) {
        return null;
    }

    @Override
    public List<InstrumentTrade> getInstrumentTrade(String instrument_name) {
        HttpParameter parameter = HttpParameter.build();
        parameter.add("instrument_name", instrument_name.toUpperCase());
        String url = URL + TRADE_BY_INSTRUMENT + "?" + parameter.concat();
        String jsonStr = OkHttpV3ClientProxy.get(url, "Authorization", getToken());
        JSONObject jo = JSONObject.parseObject(jsonStr);
        return DeribitTradeUtil.parseInstrumentTrade(jo);
    }

    public List<Order> getOrders_currency(String currency) {
        HttpParameter parameter = HttpParameter.build();
        parameter.add("currency", currency.toUpperCase());
        String url = URL + ORDERS_currency + "?" + parameter.concat();
        String jsonStr = OkHttpV3ClientProxy.get(url, "Authorization", getToken());
        JSONObject jo = JSONObject.parseObject(jsonStr);
        return DeribitTradeUtil.parseOrders(jo);
    }


    public List<Order> getOrders_instrument(String instrument_name) {
        HttpParameter parameter = HttpParameter.build();
        parameter.add("instrument_name", instrument_name);
        String url = URL + ORDERS_instrument + "?" + parameter.concat();
        String jsonStr = OkHttpV3ClientProxy.get(url, "Authorization", getToken());
        JSONObject jo = JSONObject.parseObject(jsonStr);
        return DeribitTradeUtil.parseOrders(jo);
    }

    /**
     * @param type (all , limit , stop_all , stop_limit , stop_market)
     **/
    public List<Order> getOrders_instrument(String instrument_name, String type) {
        HttpParameter parameter = HttpParameter.build();
        parameter.add("instrument_name", instrument_name);
        parameter.add("type", type);
        String url = URL + ORDERS_instrument + "?" + parameter.concat();
        String jsonStr = OkHttpV3ClientProxy.get(url, "Authorization", getToken());
        JSONObject jo = JSONObject.parseObject(jsonStr);
        return DeribitTradeUtil.parseOrders(jo);
    }


    public Order getOrder(String orderId) {
        // 拼接URL
        HttpParameter parameter = HttpParameter.build();
        parameter.add("order_id", "orderId");
        String url = URL + ORDER + "?" + parameter.concat();
        String result = OkHttpV3ClientProxy.get(url, "Authorization", getToken());
        JSONObject jo = JSONObject.parseObject(result);
        return DeribitTradeUtil.parseOrder(jo);
    }

    @Override
    public List<Trade> getTrade(String symbol, String orderId) {
        return null;
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

    /**
     * BASE64 签名
     */
    private String getAuthorization_Base64() {
        // Authorization: Basic BASE64(ClientId + : + ClientSecret)
        // 使用BASE64加密算法对密钥进行签名
        String value = ACCESS_KEY + ":" + ACCESS_SECRET;
        String signature = null;
        try {
            signature = Base64.getEncoder().encodeToString(value.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return ("Basic " + signature);
    }

    private String getToken() {
        if (tokenType == 0) {
            return getAuthorization_Base64();
        } else if (tokenType == 1) {
            return Authorization_HmacSHA256();
        } else if (tokenType == 2) {
            return getBearer();
        }
        return "";
    }

    /**
     * HmacSHA256 签名
     */
    private String Authorization_HmacSHA256() {
        // Signature=$( echo -ne "${Timestamp}\n${Nonce}\n${HttpMethod}\n${URI}\n${Body}\n" | openssl sha256 -r -hmac "$ClientSecret" | cut -f1 -d' ' )

        String body = "";
        String method = "GET";
        Long timestamp = System.currentTimeMillis();
        // 生成随机字符串（长度为9）
        String nonce = RandomUtil.randomString(RandomUtil.BASE_CHAR_NUMBER, 9);

        // 拼接URI
        HttpParameter uriParameter = HttpParameter.build();
        uriParameter.add("currency", "BTC");
        String uri = ACCOUNT + "?" + uriParameter.concat();

        // 使用HmacSHA256加密算法对URI进行签名
        String stringToSign = timestamp + "\n" + nonce + "\n" + method + "\n" + uri + "\n" + body + "\n";
        String signature = HmacUtil.hmacSHA256(stringToSign, ACCESS_SECRET);

        // curl -s -X ${HttpMethod} -H "Authorization: deri-hmac-sha256 id=${ClientId},ts=${Timestamp},nonce=${Nonce},sig=${Signature}" "https://www.deribit.com${URI}"
        // 拼接Http Header的参数
        HttpParameter headerParameter = HttpParameter.build();
        headerParameter.add("id", ACCESS_KEY);
        headerParameter.add("ts", timestamp);
        headerParameter.add("nonce", nonce);
        headerParameter.add("sig", signature);
        String headerParameterString = headerParameter.concat("=", ",", null);

        return ("deri-hmac-sha256 " + headerParameterString);
    }

    /**
     * 测试API接口：/api/v2/public/auth
     */
    private String getBearer() {
        HttpParameter parameter = HttpParameter.build();
        parameter.add("client_id", ACCESS_KEY);
        parameter.add("client_secret", ACCESS_SECRET);
        parameter.add("grant_type", "client_credentials");
        parameter.add("scope", "session:apiconsole-uq8wq7vjf7f");
        String url = URL + AUTH + "?" + parameter.concat();
        String result = OkHttpV3ClientProxy.get(url);
        log.info(result);
        return ("Bearer " + JSONObject.parseObject(result).getJSONObject("result").getString("access_token"));
    }

    @Override
    public List<Position> getPositions(String symbol) {
        String url = URL + CONTRACT;
        String result = OkHttpV3ClientProxy.get(url, "Authorization", getToken());
        JSONObject jo = JSONObject.parseObject(result);
        return DeribitTradeUtil.parsePosition(jo);
    }

    @Override
    public Boolean cancelAll(String instrument) {
        return null;
    }

    @Override
    public String triggerCloseBuy(String instrument, BigDecimal triggerPrice, BigDecimal quantity, Integer leverRate) {
        return null;
    }

    @Override
    public String triggerCloseSell(String instrument, BigDecimal triggerPrice, BigDecimal quantity, Integer leverRate) {
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

    public static void main(String[] args) {
        DeribitTradeExchange derbit = new DeribitTradeExchange("L7zRRRbJ", "xNLeUgj9o_Pu_Fp8ioQLBX-sqQxSeUZQiw5STJCw27Y");
        derbit.buyLimit("BTC-PERPETUAL", new BigDecimal("9400"), new BigDecimal("10"));
//        derbit.sellLimit("BTC-PERPETUAL",new BigDecimal("9500"),new BigDecimal("10"));
    }

}
