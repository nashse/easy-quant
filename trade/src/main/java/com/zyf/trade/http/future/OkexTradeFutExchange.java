package com.zyf.trade.http.future;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.ITradeExchange;
import com.zyf.baseservice.util.okex.OkexFutUtil;
import com.zyf.baseservice.util.okex.OkexSignUtil;
import com.zyf.baseservice.util.okex.OkexUtil;
import com.zyf.common.model.*;
import com.zyf.common.model.enums.HttpMethod;
import com.zyf.common.model.enums.Side;
import com.zyf.common.model.enums.Type;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;
import javafx.geometry.Pos;
import netscape.javascript.JSObject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * okex期货交易类
 *
 * @author yuanfeng.z
 * @date 2020/7/29 20:10
 */
public class OkexTradeFutExchange implements ITradeExchange {

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
    private static final String ACCOUNT = "/api/swap/v3/<instrument_id>/accounts";

    /**
     * 下单资源路径
     */
    private static final String ORDER = "/api/swap/v3/order";

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
    private static final String GET_ALLORDERS = "/api/swap/v3/orders/<instrument_id>";

    /**
     * 获取成交明细
     */
    private static final String GET_TRADE = "/api/spot/v3/fills";

    /**
     * 批量撤销订单
     */
    private static final String BATCH_ORDERS = "/api/swap/v3/cancel_batch_orders/<instrument_id>";

    /**
     * 止盈止损下单
     */
    private static final String TRIGGER_ORDER = "/api/swap/v3/order_algo";

    /**
     * 止盈止损下单
     */
    private static final String TRIGGER_CANCEL_ALL = "/api/swap/v3/cancel_algos";

    /**
     * 获取止盈止损订单
     */
    private static final String GET_TRIGGER_ALL_ORDERS = "/api/swap/v3/order_algo/<instrument_id>";

    /**
     * 设置杠杆
     */
    private static final String SET_LEVERAGE = "/api/swap/v3/accounts/<instrument_id>/leverage";

    /**
     * 获取所有仓位
     */
    private static final String GET_POSITION = "/api/swap/v3/<instrument_id>/position";

    /**
     * 获取订单信息
     */
    private static final String GET_ORDER = "/api/swap/v3/orders/<instrument_id>/<order_id>";

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

    public OkexTradeFutExchange(String accessKey, String secretKey) {
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
        JSONObject rCurrency = getBalance(symbol);
        return OkexFutUtil.parseAccount(symbol, null, rCurrency);
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
        final String batchOrders = BATCH_ORDERS.replace("<instrument_id>", symbol);

        /*下单*/
        LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.put("ids", ids);
        paramMap.put("instrument_id", symbol);

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.POST.getValue(),
                URL, paramMap, batchOrders, this.accessKey, this.secretKey, passphrase);
        String url = URL + batchOrders;
        String json = OkHttpV3ClientProxy.postHeader(url, linkedHashMap, OkexUtil.addParams(paramMap));
        JSONObject j = JSONObject.parseObject(json);

        if ("0".equals(j.getString("error_code"))) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<Order> getOrders(String symbol) {
        return null;
    }

    @Override
    public List<Order> getPendingOrders(String symbol) {
        final String GETALLORDERS = GET_ALLORDERS.replace("<instrument_id>", symbol);

        /*下单*/
        LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
        final String state = "6";
        String param = "?instrument_id=" + symbol + "&state=" + state;

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.GET.getValue(),
                URL, null, GETALLORDERS + param, this.accessKey, this.secretKey, passphrase);
        String url = URL + GETALLORDERS + param;
        String json = OkHttpV3ClientProxy.getHeader(url, linkedHashMap);

        return OkexFutUtil.parseOrders(JSONObject.parseObject(json));
    }

    @Override
    public Order getOrder(String symbol, String orderId) {
        final String GETORDER = GET_ORDER.
                replace("<instrument_id>", symbol).
                replace("<order_id>", orderId);

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.GET.getValue(),
                URL, null, GETORDER, this.accessKey, this.secretKey, passphrase);
        String url = URL + GETORDER;
        String json = OkHttpV3ClientProxy.getHeader(url, linkedHashMap);

        return OkexFutUtil.parseOrder(JSONObject.parseObject(json));
    }

    @Override
    public List<Trade> getTrade(String symbol, String orderId) {
        return null;
    }

    @Override
    public String openLong(String instrument,
                           BigDecimal price,
                           BigDecimal quantity,
                           Integer leverRate) {
        return this.order(instrument, price, quantity, 1, 0, 2);
    }

    @Override
    public String openShort(String instrument,
                            BigDecimal price,
                            BigDecimal quantity,
                            Integer leverRate) {
        return this.order(instrument, price, quantity, 2, 0, 2);
    }

    @Override
    public String closeLong(String instrument,
                            BigDecimal price,
                            BigDecimal quantity,
                            Integer leverRate) {
        return this.order(instrument, price, quantity, 3, 0, 2);
    }

    @Override
    public String closeShort(String instrument,
                             BigDecimal price,
                             BigDecimal quantity,
                             Integer leverRate) {
        return this.order(instrument, price, quantity, 4, 0, 2);
    }

    @Override
    public String openLongMarket(String instrument, BigDecimal quantity, Integer leverRate) {
        return this.order(instrument, null, quantity, 1, 0, 4);
    }

    @Override
    public String openShortMarket(String instrument, BigDecimal quantity, Integer leverRate) {
        return this.order(instrument, null, quantity, 2, 0, 4);
    }

    @Override
    public String closeLongMarket(String instrument, BigDecimal quantity, Integer leverRate) {
        return this.order(instrument, null, quantity, 3, 0, 4);
    }

    @Override
    public String closeShortMarket(String instrument, BigDecimal quantity, Integer leverRate) {
        return this.order(instrument, null, quantity, 4, 0, 4);
    }

    /**
     * 下单
     * @param instrument 合約
     * @param price 價格
     * @param quantity 数量
     * @param type 1:开多 2:开空 3:平多 4:平空
     * @param matchPrice 是否以对手价下单。0:不是; 1:是。当以对手价下单，order_type只能选择0（普通委托）
     * @param orderType  0：普通委托 1：只做Maker（Post only） 2：全部成交或立即取消（FOK） 3：立即成交并取消剩余（IOC） 4：市价委托
     * @return
     */
    public String order(String instrument,
                          BigDecimal price,
                          BigDecimal quantity,
                          Integer type,
                          Integer matchPrice,
                          Integer orderType) {
        /*下单*/
        LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
        /**
         * type可填参数：
         * 1:开多
         * 2:开空
         * 3:平多
         * 4:平空
         */
        paramMap.put("type", type);
        paramMap.put("match_price", matchPrice);
        paramMap.put("instrument_id", instrument);
        paramMap.put("order_type", orderType);
        paramMap.put("price", price);
        paramMap.put("size", quantity);

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.POST.getValue(),
                URL, paramMap, ORDER, this.accessKey, this.secretKey, passphrase);
        String url = URL + ORDER;

        String json = OkHttpV3ClientProxy.postHeader(url, linkedHashMap, OkexUtil.addParams(paramMap));

        if ("33014".equals(JSONObject.parseObject(json).getString("code"))) {
            return "订单不存在";
        }

        JSONObject j = JSONObject.parseObject(json);
        return j.getString("order_id");
    }

    @Override
    public List<InstrumentTrade> getInstrumentTrade(String symbol) {
        return null;
    }

    @Override
    public List<Position> getPositions(String instrument) {
        final String GETPOSITION = GET_POSITION.replace("<instrument_id>", instrument);

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.GET.getValue(),
                URL, null, GETPOSITION, this.accessKey, this.secretKey, passphrase);
        String url = URL + GETPOSITION;
        String json = OkHttpV3ClientProxy.getHeader(url, linkedHashMap);

        return OkexFutUtil.parsePositions(JSONObject.parseObject(json));
    }

    @Override
    public Boolean cancelAll(String instrument) {
        return null;
    }

    @Override
    public String triggerCloseBuy(String instrument,
                                  BigDecimal triggerPrice,
                                  BigDecimal quantity,
                                  Integer leverRate) {
        return this.triggerOrder(instrument, triggerPrice, triggerPrice, quantity, 3, 2, leverRate);
    }

    @Override
    public String triggerCloseSell(String instrument,
                                   BigDecimal triggerPrice,
                                   BigDecimal quantity,
                                   Integer leverRate) {
        return this.triggerOrder(instrument, triggerPrice, triggerPrice, quantity, 4, 2, leverRate);
    }

    @Override
    public List<Order> getTriggerPendingOrders(String instrument) {
        final String triggerAllOrders = GET_TRIGGER_ALL_ORDERS.replace("<instrument_id>", instrument);

        /*下单*/
        final String state = "1";
        String param = "?instrument_id=" + instrument + "&order_type=1" + "&status=" + state;

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.GET.getValue(),
                URL, null, triggerAllOrders + param, this.accessKey, this.secretKey, passphrase);
        String url = URL + triggerAllOrders + param;
        String json = OkHttpV3ClientProxy.getHeader(url, linkedHashMap);
        JSONObject j = JSONObject.parseObject(json);

        return OkexFutUtil.parseTriggerPendingOrders(j);
    }

    @Override
    public Boolean triggerCancelAll(String instrument) {
        return null;
    }

    @Override
    public Boolean triggerCancelOrders(String instrument, List<String> ids) {
        /*下单*/
        LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.put("instrument_id", instrument);
        paramMap.put("algo_ids", ids);
        paramMap.put("order_type", "1");

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.POST.getValue(),
                URL, paramMap, TRIGGER_CANCEL_ALL, this.accessKey, this.secretKey, passphrase);
        String url = URL + TRIGGER_CANCEL_ALL;
        String json = OkHttpV3ClientProxy.postHeader(url, linkedHashMap, OkexFutUtil.addParams(paramMap));
        JSONObject j = JSONObject.parseObject(json);

        return OkexFutUtil.parseTriggerCancelOrders(j);
    }

    @Override
    public Boolean setLeverage(String instrument, BigDecimal leverage) {
        final String SETLEVERAGE = SET_LEVERAGE.replace("<instrument_id>", instrument);
        /*下单*/
        LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
        paramMap.put("leverage", leverage);
        /**
         * 方向
         * 1:逐仓-多仓
         * 2:逐仓-空仓
         * 3:全仓
         */
        paramMap.put("side", 3);

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.POST.getValue(),
                URL, paramMap, SETLEVERAGE, this.accessKey, this.secretKey, passphrase);
        String url = URL + SETLEVERAGE;
        String json = OkHttpV3ClientProxy.postHeader(url, linkedHashMap, OkexFutUtil.addParams(paramMap));

        return true;
    }

    /**
     * 获取单个币种余额信息
     *
     * @param symbol
     * @return
     */
    public JSONObject getBalance(String symbol) {
        String accountInfo = ACCOUNT.replace("<instrument_id>", symbol);
        LinkedHashMap<String, String> lh = OkexSignUtil.sign(METHOD_GET, URL, null, accountInfo, accessKey, secretKey, passphrase);
        return JSON.parseObject(OkHttpV3ClientProxy.getHeader(URL + accountInfo, lh));
    }

    /**
     * 止盈止损下单
     * @param instrument 合约名
     * @param triggerPrice 触发价格
     * @param price 价格
     * @param quantity 数量
     * @param type 类型
     * @param limitMarket 类型 1:限价 2:市场价；触发价格类型，默认是限价；为市场价时，委托价格不必填；
     * @param leverRate 杠杆
     * @return
     */
    public String triggerOrder(String instrument,
                                  BigDecimal triggerPrice,
                                  BigDecimal price,
                                  BigDecimal quantity,
                                  Integer type,
                                  Integer limitMarket,
                                  Integer leverRate) {
        /*下单*/
        LinkedHashMap<String, Object> paramMap = new LinkedHashMap<>();
        /**
         * type可填参数：
         * 1:开多
         * 2:开空
         * 3:平多
         * 4:平空
         */
        paramMap.put("type", type);
        paramMap.put("instrument_id", instrument);
        paramMap.put("order_type", "1");
        paramMap.put("size", quantity);
        paramMap.put("trigger_price", triggerPrice);
        paramMap.put("type", type);
        paramMap.put("algo_type", limitMarket);
        if (limitMarket == 1) {
            paramMap.put("algo_price", price);
        }

        LinkedHashMap linkedHashMap = OkexSignUtil.sign(HttpMethod.POST.getValue(),
                URL, paramMap, TRIGGER_ORDER, this.accessKey, this.secretKey, passphrase);
        String url = URL + TRIGGER_ORDER;
        String json = OkHttpV3ClientProxy.postHeader(url, linkedHashMap, OkexUtil.addParams(paramMap));

        if ("33014".equals(JSONObject.parseObject(json).getString("code"))) {
            return "订单不存在";
        }
        JSONObject j = JSONObject.parseObject(json);

        return OkexFutUtil.parseTriggerOrder(j);
    }
}
