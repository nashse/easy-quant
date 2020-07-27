package com.zyf.trade.http.huobipro;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.baseexchange.BaseHuobiProExchange;
import com.zyf.baseservice.util.huobipro.HuobiProSignUtil;
import com.zyf.baseservice.util.huobipro.HuobiProUtil;
import com.zyf.common.model.Account;
import com.zyf.common.model.Order;
import com.zyf.common.model.Trade;
import com.zyf.common.model.enums.HttpMethod;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 火币pro交易所交易实现类
 * @author yuanfeng.z
 * @date 2019/6/28 12:00
 */
public class HuobiProTradeExchange extends BaseHuobiProExchange {
    /**
     * 协议
     */
    private static final String PROTOCOL = "https://";

    /**
     * 网站
     */
    private static final String SITE = "api.huobi.pro";

    /**
     * url
     */
    private static final String URL = PROTOCOL +SITE;

    /**
     * 账号信息（账号id）
     */
    private static final String ACCOUNT_INFO = "/v1/account/accounts";

    /**
     * 账号资产信息
     */
    private static final String ACCOUNT = "/v1/account/accounts/{account-id}/balance";

    /**
     * 下单资源路径
     */
    private static final String ORDER = "/v1/order/orders/place";

    /**
     * 根据订单id获取订单信息资源路径
     */
    private static final String ORDER_BY_ID = "/v1/order/orders/{order-id}";

    /**
     * 撤销单个订单资源路径
     */
    private static final String CANCEL_ORDER = "/v1/order/orders/{order-id}/submitcancel";

    /**
     * 批量撤销订单资源路径
     */
    private static final String CANCEL_ALLORDER = "/v1/order/orders/batchcancel";

    /**
     * 获取单个订单资源路径
     */
    private static final String GET_ORDER = "/v1/order/orders/{order-id}";

    /**
     * 获取单个订单资源路径
     */
    private static final String GET_ALLORDER = "/v1/order/orders";

    /**
     * 获取成交明细资源路径
     */
    private static final String TRADE = "/v1/order/orders/{order-id}/matchresults";

    /**
     * orderId占位符
     */
    private static final String ORDERID = "{order-id}";


    /*********对象属性***********/

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

    public HuobiProTradeExchange(String accessKey, String secretKey) {
        this.accessKey = accessKey;
        this.secretKey = secretKey;
        this.accountId = this.getAccountId();
    }

    /**
     * 获取账户信息
     * @param symbol
     * @return
     */
    @Override
    public Account getAccount(String symbol) {
        // 获取accountId
        String resourcePath = ACCOUNT;
        final String accountIdStr = "{account-id}";
        resourcePath = resourcePath.replace(accountIdStr, this.accountId);

        /*获取Account数据*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.GET,
                PROTOCOL,
                SITE,
                resourcePath,
                this.accessKey,
                this.secretKey,
                paramMap);
        String accountStr = OkHttpV3ClientProxy.get(url);

        /*适配*/
        JSONObject jo = JSONObject.parseObject(accountStr);
        Account account = HuobiProUtil.parseAccount(symbol, jo);
        return account;
    }

    /**
     * 市价买入
     * @param symbol 币对
     * @param cost 费用
     * @return 订单id
     */
    @Override
    public String buyMarket(String symbol, BigDecimal cost) {
        return buyOrSellMarket(symbol, cost, 1);
    }

    public String buyOrSellMarket(String symbol, BigDecimal costOrQuantity, Integer butOrSell){
        // 转换币对格式
        symbol = HuobiProUtil.transfSymbol(symbol);

        int buy = 1;
        int sell = 0;

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
        map.put("account-id", this.accountId);
        map.put("symbol", symbol);
        if(butOrSell == buy){
            map.put("type", "buy-market");
        }else {
            map.put("type", "sell-market");
        }
        map.put("amount", costOrQuantity);


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
     * 市价卖出
     * @param symbol 币对
     * @param quantity 卖出数量
     * @return 订单id
     */
    @Override
    public String sellMarket(String symbol, BigDecimal quantity) {
        return buyOrSellMarket(symbol, quantity, 0);
    }


    /**
     * 限价买入
     * @param symbol 币对
     * @param price 价格
     * @param quantity 数量
     * @return 订单id
     */
    @Override
    public String buyLimit(String symbol, BigDecimal price, BigDecimal quantity) {
        return buyOrSellLimit(symbol, price, quantity, 1);
    }

    public String buyOrSellLimit(String symbol, BigDecimal price, BigDecimal quantity, Integer butOrSell){
        // 转换币对格式
        symbol = HuobiProUtil.transfSymbol(symbol);

        int buy = 1;
        int sell = 0;

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE, ORDER,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("account-id", this.accountId);
        map.put("symbol", symbol);
        if(butOrSell == buy){
            map.put("type", "buy-limit");
        }else {
            map.put("type", "sell-limit");
        }
        map.put("price", price);
        map.put("amount", quantity);
        String orderId = OkHttpV3ClientProxy.post(url, JSON.toJSONString(map));

        return orderId;
    }

    /**
     * 限价卖出
     * @param symbol 币对
     * @param price 价格
     * @param quantity 数量
     * @return 订单id
     */
    @Override
    public String sellLimit(String symbol, BigDecimal price, BigDecimal quantity) {
        return buyOrSellLimit(symbol, price, quantity, 0);
    }


    /**
     * 根据订单id撤单
     * @param symbol 币对
     * @param orderId 订单id
     * @return
     */
    @Override
    public Boolean cancelOrder(String symbol, String orderId) {

        // 获取orderId
        String resourcePath = CANCEL_ORDER;
        resourcePath = resourcePath.replace(ORDERID, orderId);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                resourcePath,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, Object> map = new HashMap<>();
        map.put("order-id", orderId);
        String data =
                JSONObject.parseObject(OkHttpV3ClientProxy.post(url, JSON.toJSONString(map))).getString("data");

        if(data != null){
            return true;
        }
        return false;
    }

    /**
     * 批量撤单
     * @param symbol 币对
     * @param ids
     * @return
     */
    @Override
    public Boolean cancelOrders(String symbol, List<String> ids) {

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.POST,
                PROTOCOL,
                SITE,
                CANCEL_ALLORDER,
                this.accessKey,
                this.secretKey,
                paramMap);
        Map<String, List> map = new HashMap<>(1);
        map.put("order-ids", ids);

        JSONArray success = JSONObject.
                parseObject(OkHttpV3ClientProxy.post(url, JSON.toJSONString(map))).
                getJSONObject("data").
                getJSONArray("success");

        if(success.size() != 0){
            return true;
        }
        return false;
    }

    /**
     * 获取所有的订单
     * @param symbol 币对
     * @return 订单列表
     */
    @Override
    public List<Order> getOrders(String symbol) {

        // 转换币对格式
        symbol = HuobiProUtil.transfSymbol(symbol);

        //订单状态
        String status = "submitted,partial-filled,partial-canceled,filled,canceled";

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        paramMap.put("symbol", symbol);
        paramMap.put("states", status);

        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.GET,
                PROTOCOL,
                SITE,
                GET_ALLORDER,
                this.accessKey,
                this.secretKey,
                paramMap);
        JSONArray order =
                JSONObject.parseObject(OkHttpV3ClientProxy.get(url)).getJSONArray("data");

        return HuobiProUtil.parseOrders(order);
    }

    /**
     * 获取指定id的订单信息
     * @param symbol 币对
     * @param orderId 订单id
     * @return 订单
     */
    @Override
    public Order getOrder(String symbol, String orderId) {

        // 转换币对格式
        symbol = HuobiProUtil.transfSymbol(symbol);

        // 获取orderId
        String resourcePath = GET_ORDER;
        resourcePath = resourcePath.replace(ORDERID, orderId);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.GET,
                PROTOCOL,
                SITE,
                resourcePath,
                this.accessKey,
                this.secretKey,
                paramMap);
        JSONObject order =
                JSONObject.parseObject(OkHttpV3ClientProxy.get(url)).getJSONObject("data");

        return HuobiProUtil.parseOrder(order);
    }

    /**
     * 获取成交明细
     * @param symbol
     * @param orderId
     * @return
     */
    @Override
    public List<Trade> getTrade(String symbol, String orderId) {

        // 转换币对格式
        symbol = HuobiProUtil.transfSymbol(symbol);

        // 获取orderId
        String resourcePath = TRADE;
        resourcePath = resourcePath.replace(ORDERID, orderId);

        /*下单*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.GET,
                PROTOCOL,
                SITE,
                resourcePath,
                this.accessKey,
                this.secretKey,
                paramMap);
        JSONObject trade = JSONObject.parseObject(OkHttpV3ClientProxy.get(url));

        return HuobiProUtil.parseTrades(trade.getJSONArray("data"), orderId);
    }

    /**
     * 获取账户id
     * @return
     */
    private String getAccountId() {
        /*获取AccountInfo数据*/
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        String url = HuobiProSignUtil.signRequestUrl(HttpMethod.GET,
                PROTOCOL,
                SITE,
                ACCOUNT_INFO,
                this.accessKey,
                this.secretKey,
                paramMap);

        /*获取accountId*/
        String accountInfoStr = OkHttpV3ClientProxy.get(url);
        JSONObject jo = JSONObject.parseObject(accountInfoStr);
        String accountId = HuobiProUtil.parseAccountInfo(jo);

        return accountId;
    }
}
