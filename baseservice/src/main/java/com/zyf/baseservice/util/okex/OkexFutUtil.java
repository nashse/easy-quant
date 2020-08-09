package com.zyf.baseservice.util.okex;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyf.common.model.Account;
import com.zyf.common.model.Balance;
import com.zyf.common.model.Order;
import com.zyf.common.model.Position;
import com.zyf.common.model.enums.Side;
import com.zyf.common.model.enums.State;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * okex期货工具类
 * @author yuanfeng.z
 * @date 2019/6/27 11:32
 */
public class OkexFutUtil {
    /**
     * 将币对拆分为币对
     * @param instrument 币对
     * @return
     */
    public static String[] splitInstrument(String instrument) {
        String[] arg = instrument.split("-");
        return new String[]{arg[0], arg[1]};
    }

    /**
     * 序列化account数据
     * @param symbol 币对
     * @param l json格式的balance数据
     * @param json json格式的balance数据
     * @return
     */
    public static Account parseAccount(String symbol, JSONObject l, JSONObject json) {
        BigDecimal lAvailable = BigDecimal.ZERO;
        BigDecimal lFrozen = BigDecimal.ZERO;
        BigDecimal rAvailable = BigDecimal.ZERO;
        BigDecimal rFrozen = BigDecimal.ZERO;

        String[] arr = OkexFutUtil.splitInstrument(symbol);

        if (json == null) { return null; }

        JSONObject rJson = json.getJSONObject("info");
        rAvailable = rJson.getBigDecimal("total_avail_balance");
        rFrozen = rJson.getBigDecimal("margin_frozen");

        return new Account(null, symbol,
                new Balance(null, arr[0], lAvailable, lFrozen),
                new Balance(rJson.toJSONString(), arr[1], rAvailable, rFrozen));

    }

    /**
     * 序列化orders
     * @param json json格式的orders数据
     * @return orders
     */
    public static List<Order> parseOrders(JSONObject json) {
        JSONArray array = json.getJSONArray("order_info");
        List<Order> orders = new LinkedList<>();
        for (int i = 0; i < array.size(); i++) {
            orders.add(parseOrder(array.getJSONObject(i)));
        }
        return orders;
    }

    /**
     * 解析Order
     * @param json 原始数据
     * @return Order
     */
    public static Order parseOrder(JSONObject json) {

        /*
        {
            "client_oid":"oktspot70",
            "created_at":"2019-03-15T02:52:56.000Z",
            "filled_notional":"3.8886",
            "filled_size":"0.001",
            "funds":"",
            "instrument_id":"BTC-USDT",
            "notional":"",
            "order_id":"2482659399697408",
            "order_type":"0",
            "price":"3927.3",
            "product_id":"BTC-USDT",
            "side":"buy",
            "size":"0.001",
            "status":"filled",
             "state":"2",
            "timestamp":"2019-03-15T02:52:56.000Z",
            "type":"limit"
         }
        */

        Long time = json.getDate("timestamp").getTime();
        String id = json.getString("algo_id") != null ? json.getString("algo_id") : json.getString("order_id");
        BigDecimal price = json.getBigDecimal("trigger_price");
        BigDecimal quantity = json.getBigDecimal("size");
        BigDecimal deal = json.getBigDecimal("real_amount");
        State state = null;
        Side side = null;

        switch (json.getInteger("type")) {
            case 1:
                side = Side.OPEN_LONG;
                break;
            case 2:
                side = Side.OPEN_SHORT;
                break;
            case 3:
                side = Side.CLOSE_LONG;
                break;
            case 4:
                side = Side.CLOSE_SHORT;
                break;
            default:
        }

        return new Order(json.toJSONString(), time, id, price, quantity, deal, side, null, state);
    }


    /**
     * 序列化撤销计划订单
     * @param json 交易所返回数据
     * @return
     */
    public static Boolean parseTriggerCancelOrders(JSONObject json) {
        if (json.getInteger("code").equals(0)) {
            return true;
        }

        return false;
    }

    /**
     * 序列化计划下单
     * @param json 交易所返回数据
     * @return
     */
    public static String parseTriggerOrder(JSONObject json) {
        if (json.getInteger("code").equals(0)) {
            return json.getJSONObject("data").getString("algo_id");
        }

        return null;
    }

    /**
     * 序列化triggerPendingOrders
     * @param json json格式的triggerPendingOrders数据
     * @return orders
     */
    public static List<Order> parseTriggerPendingOrders(JSONObject json) {
        JSONArray array = json.getJSONArray("orderStrategyVOS");
        List<Order> orders = new LinkedList<>();
        for (int i = 0; i < array.size(); i++) {
            orders.add(parseOrder(array.getJSONObject(i)));
        }
        return orders;
    }

    /**
     * 拼接参数
     *
     * @param paramMap
     */
    public static String addParams(LinkedHashMap<String, Object> paramMap) {
        return JSON.toJSONString(paramMap);
    }

    /**
     * 序列化positions
     * @param json json格式的positions数据
     * @return positions
     */
    public static List<Position> parsePositions(JSONObject json) {
        JSONArray array = json.getJSONArray("holding");
        List<Position> positions = new LinkedList<>();
        for (int i = 0; i < array.size(); i++) {
            positions.add(parsePosition(array.getJSONObject(i)));
        }
        return positions;
    }

    /**
     * 序列化position
     * @param json json格式的position数据
     * @return positions
     */
    public static Position parsePosition(JSONObject json) {
        Long timestamp = json.getDate("timestamp").getTime();
        String instrument = json.getString("instrument_id");
        BigDecimal liquidationPrice = json.getBigDecimal("liquidation_price");
        BigDecimal quantity = json.getBigDecimal("position");
        BigDecimal availQuantity = json.getBigDecimal("avail_position");
        BigDecimal avgCost = json.getBigDecimal("avg_cost");
        BigDecimal settlementPrice = json.getBigDecimal("settlement_price");
        BigDecimal settledPnl = json.getBigDecimal("settled_pnl");
        BigDecimal unrealizedPnl = json.getBigDecimal("unrealized_pnl");
        BigDecimal last = json.getBigDecimal("last");
        BigDecimal margin = json.getBigDecimal("margin");
        Side side = null;
        if ("long".equals(json.getString("side"))) {
            side = Side.OPEN_LONG;
        } else if ("short".equals(json.getString("side"))) {
            side = Side.OPEN_SHORT;
        }

        return new Position(json.toJSONString(), timestamp, instrument, liquidationPrice,
                quantity, availQuantity, avgCost, settlementPrice, settledPnl,
                unrealizedPnl, last, margin, side);
    }
}
