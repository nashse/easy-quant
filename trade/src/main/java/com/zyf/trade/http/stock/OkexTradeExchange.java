package com.zyf.trade.http.stock;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.ITradeExchange;
import com.zyf.baseservice.util.okex.OkexSignUtil;
import com.zyf.baseservice.util.okex.OkexUtil;
import com.zyf.common.model.*;
import com.zyf.common.model.enums.HttpMethod;
import com.zyf.common.model.enums.Side;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * okex交易所交易实现类
 *
 * @author yuanfeng.z
 * @date 2019/6/29 10:00
 */
@Slf4j
public class OkexTradeExchange implements ITradeExchange {

    /**
     * 协议
     */
    private static final String PROTOCOL = "https://";

    /**
     * 网站
     */
    private static final String SITE = "www.okex.com";

    /**
     * url
     */
    private static final String URL = PROTOCOL + SITE;

    /**
     * get请求方法
     */
    protected static final String METHOD_GET = "GET";

    /**
     * 账号资产信息
     */
    private static final String ACCOUNT = "/api/spot/v3/accounts/";

    /**
     * 下单资源路径
     */
    private static final String ORDER = "/api/spot/v3/orders";

    /**
     * 撤销单资源路径
     */
    private static final String CANCEL_ORDER = "/api/spot/v3/cancel_orders/";

    /**
     * 批量撤销单资源路径
     */
    private static final String CANCEL_ORDER_BATCH = "/api/spot/v3/cancel_batch_orders";

    /**
     * 获取所有订单资源路径
     */
    private static final String GET_ALLORDERS = "/api/spot/v3/orders";

    /**
     * 获取成交明细
     */
    private static final String GET_TRADE = "/api/spot/v3/fills";

    /**
     * 公钥
     */
    private String accessKey = "";

    /**
     * 密钥
     */
    private String secretKey = "";

    /**
     * 密钥
     */
    private String passphrase = "";

    public OkexTradeExchange(String accessKey, String secretKey) {
        this.accessKey = accessKey.split("/")[0];
        this.secretKey = secretKey;
        this.passphrase = accessKey.split("/")[1];
    }

    @Override
    public Map<String, Precision> getPrecisions() {
        return null;
    }

    /**
     * 获取账户信息
     *
     * @param symbol
     * @return
     */
    @Override
    public Account getAccount(String symbol) {

        String[] arr = OkexUtil.transfSymbolToArr(symbol);
        // 转换币对格式
        String leftSymbol = arr[0];
        String rightSymbol = arr[1];
        JSONObject lCurrency = getBalance(leftSymbol);
        JSONObject rCurrency = getBalance(rightSymbol);

        return OkexUtil.parseAccount(symbol, lCurrency, rCurrency);
    }

    /**
     * 限价买入
     *
     * @param symbol   币对
     * @param price    价格
     * @param quantity 数量
     * @return 订单id
     */
    @Override
    public String buyLimit(String symbol, BigDecimal price, BigDecimal quantity) {
        return buyOrSellLimit(symbol, price, quantity, 1);
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

    /**
     * 限价卖出
     *
     * @param symbol   币对
     * @param price    价格
     * @param quantity 数量
     * @return 订单id
     */
    @Override
    public String sellLimit(String symbol, BigDecimal price, BigDecimal quantity) {
        return buyOrSellLimit(symbol, price, quantity, 0);
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

    public String buyOrSellLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer buyOrSell) {

        symbol = OkexUtil.transfSymbol(symbol);
        int buy = 1;
        int sell = 0;

        /*下单*/
        LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.put("type", "limit");
        if (buyOrSell == buy) {
            paramMap.put("side", "buy");
        } else {
            paramMap.put("side", "sell");
        }
        paramMap.put("instrument_id", symbol);
        paramMap.put("order_type", "0");
        paramMap.put("price", price);
        paramMap.put("size", quantity);

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.POST.getValue(), URL, paramMap, ORDER, this.accessKey, this.secretKey, passphrase);
        String url = URL + ORDER;
        String json = OkHttpV3ClientProxy.postHeader(url, linkedHashMap, OkexUtil.addParams(paramMap));

        if ("33014".equals(JSONObject.parseObject(json).getString("code"))) {
            return "订单不存在";
        }

        return json;
    }


    /**
     * 市价买入
     *
     * @param symbol 币对
     * @param cost   费用
     * @return 订单id
     */
    @Override
    public String buyMarket(String symbol, BigDecimal cost) {
        return buyOrSellMarket(symbol, cost, 1);
    }

    /**
     * 市价卖出
     *
     * @param symbol   币对
     * @param quantity 卖出数量
     * @return 订单id
     */
    @Override
    public String sellMarket(String symbol, BigDecimal quantity) {
        return buyOrSellMarket(symbol, quantity, 0);
    }

    public String buyOrSellMarket(String symbol, BigDecimal costOrQuantity, Integer buyOrSell) {

        symbol = OkexUtil.transfSymbol(symbol);

        int buy = 1;
        int sell = 0;

        /*下单*/
        LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.put("type", "market");
        if (buyOrSell == buy) {
            paramMap.put("side", "buy");
        } else {
            paramMap.put("side", "sell");
        }
        paramMap.put("instrument_id", symbol);
        paramMap.put("order_type", "0");
        if (buyOrSell == buy) {
            paramMap.put("notional", costOrQuantity.toString());
        } else {
            paramMap.put("size", costOrQuantity.toString());
        }
        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.POST.getValue(), URL, paramMap, ORDER, this.accessKey, this.secretKey, passphrase);
        String url = URL + ORDER;
        String json = OkHttpV3ClientProxy.postHeader(url, linkedHashMap, OkexUtil.addParams(paramMap));

        if ("33014".equals(JSONObject.parseObject(json).getString("code"))) {
            return "订单不存在";
        }

        return json;
    }

    /**
     * 根据订单id撤单
     *
     * @param orderId 订单id
     * @return
     */
    @Override
    public Boolean cancelOrder(String symbol, String orderId) {

        symbol = OkexUtil.transfSymbolToLower(symbol);

        /*下单*/
        LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.put("instrument_id", symbol);

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.POST.getValue(), URL, paramMap, CANCEL_ORDER + orderId, this.accessKey, this.secretKey, passphrase);
        String url = URL + CANCEL_ORDER + orderId;
        String json = OkHttpV3ClientProxy.postHeader(url, linkedHashMap, OkexUtil.addParams(paramMap));

        if ("33014".equals(JSONObject.parseObject(json).getString("code"))) {
            return false;
        }
        return true;
    }

    /**
     * 批量撤单
     *
     * @param ids
     * @return
     */
    @Override
    public Boolean cancelOrders(String symbol, List<String> ids) {

        symbol = OkexUtil.transfSymbolToLower(symbol);

        String param = OkexUtil.addParamsList(symbol, ids);

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.POST.getValue(), URL, null, CANCEL_ORDER_BATCH + param, this.accessKey, this.secretKey, passphrase);
        String url = URL + CANCEL_ORDER_BATCH;
        String json = OkHttpV3ClientProxy.postHeader(url, linkedHashMap, param);

        if ("33014".equals(JSONObject.parseObject(json).getString("code"))) {
            return false;
        }
        return true;
    }


    /**
     * 获取所有的订单
     *
     * @param symbol 币对
     * @return 订单列表
     */
    @Override
    public List<Order> getOrders(String symbol) {

        symbol = OkexUtil.transfSymbolToLower(symbol);

        List<Order> cancelSuccess = getOrderByState(symbol, -1);
        List<Order> waitSuccess = getOrderByState(symbol, 0);
        List<Order> partSuccess = getOrderByState(symbol, 1);
        List<Order> completeSuccess = getOrderByState(symbol, 2);

        List<Order> list = new ArrayList<>();
        list.addAll(cancelSuccess);
        list.addAll(waitSuccess);
        list.addAll(partSuccess);
        list.addAll(completeSuccess);

        return list;

    }

    @Override
    public List<Order> getPendingOrders(String symbol) {
        return null;
    }

    /**
     * 获取单个状态的所有订单
     *
     * @param symbol 币对
     * @return 订单列表
     */
    public List<Order> getOrderByState(String symbol, Integer state) {
        String param = "?instrument_id=" + symbol + "&state=" + state;

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.GET.getValue(), URL, null, GET_ALLORDERS + param, this.accessKey, this.secretKey, passphrase);
        String url = URL + GET_ALLORDERS + param;
        String json = OkHttpV3ClientProxy.getHeader(url, linkedHashMap);

        return OkexUtil.parseOrders(JSONObject.parseArray(json));
    }

    public static void main(String[] args) {
        /*String appKey = "549cd388-d744-4e71-8290-18cb2c9a3748";
        String secret = "5B66D87B90E7C37CD4123105A6DA527B";
        String passPrase = "123456mzy";
        appKey += "/"+passPrase;
        IExchange iExchange = TradeExchangeFactory.createTradeExchange(ExchangeEnum.OKEXV3, appKey, secret);
        List<String> list= new ArrayList<>();
        iExchange.getTrade("BTC/USDT", "1");*/

    }

    /**
     * 获取指定id的订单信息
     *
     * @param symbol  币对
     * @param orderId 订单id
     * @return 订单
     */
    @Override
    public Order getOrder(String symbol, String orderId) {

        symbol = OkexUtil.transfSymbolToLower(symbol);

        String param = "/" + orderId + "?instrument_id=" + symbol;

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.GET.getValue(), URL, null, GET_ALLORDERS + param, this.accessKey, this.secretKey, passphrase);
        String url = URL + GET_ALLORDERS + param;
        String json = OkHttpV3ClientProxy.getHeader(url, linkedHashMap);

        if ("33014".equals(JSONObject.parseObject(json).getString("code"))) {
            return null;
        }

        return OkexUtil.parseOrder(JSONObject.parseObject(json));
    }

    /**
     * 获取成交明细
     *
     * @param symbol
     * @param orderId
     * @return
     */
    @Override
    public List<Trade> getTrade(String symbol, String orderId) {

        symbol = OkexUtil.transfSymbolToLower(symbol);

        String param = "?order_id=" + orderId + "&" + "instrument_id=" + symbol;

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.GET.getValue(), URL, null, GET_TRADE + param, this.accessKey, this.secretKey, passphrase);
        String url = URL + GET_TRADE + param;
        String json = OkHttpV3ClientProxy.getHeader(url, linkedHashMap);

        if (JSONObject.parseArray(json) == null) {
            return null;
        }
        return OkexUtil.parseTrades(JSONObject.parseArray(json), orderId);
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


    /**
     * 获取单个币种余额信息
     *
     * @param symbol
     * @return
     */
    public JSONObject getBalance(String symbol) {
        String account_info = ACCOUNT + symbol;
        LinkedHashMap<String, String> lh = OkexSignUtil.sign(METHOD_GET, URL, null, account_info, accessKey, secretKey, passphrase);
        return JSON.parseObject(OkHttpV3ClientProxy.getHeader(URL + account_info, lh));
    }

}
