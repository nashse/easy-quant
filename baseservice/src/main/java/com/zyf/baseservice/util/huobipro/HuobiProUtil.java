package com.zyf.baseservice.util.huobipro;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyf.common.model.*;
import com.zyf.common.model.enums.Side;
import com.zyf.common.model.enums.State;
import com.zyf.common.model.enums.Type;

import java.math.BigDecimal;
import java.util.*;

/**
 * huobipro工具类
 * @author yuanfeng.z
 * @date 2019/6/25 11:32
 */
public class HuobiProUtil {
    /**
     * 序列化ticker
     * @param jo json格式的ticker数据
     * @return
     */
    public static Ticker parseTicker(JSONObject jo) {
        if (jo == null) { return null; };
        Long timestamp = jo.getLong("ts");
        JSONObject tick = jo.getJSONObject("tick");
        if (tick == null) { return null; }
        BigDecimal open = tick.getBigDecimal("open");
        BigDecimal close = tick.getBigDecimal("close");
        BigDecimal high = tick.getBigDecimal("high");
        BigDecimal low = tick.getBigDecimal("low");
        BigDecimal vol = tick.getBigDecimal("vol");
        return new Ticker(null, timestamp, open, high, low, close, vol);
    }

    /**
     * 序列化depth
     * @param jo json格式的ticker数据
     * @return
     */
    public static Depth parseDepth(JSONObject jo) {
        if (jo == null) { return null; }
        Long timestamp = jo.getLong("ts");
        JSONObject depth = jo.getJSONObject("tick");
        if (depth == null) { return null; }
        JSONArray arrayBids = depth.getJSONArray("bids");
        List<Level> levelsBids = new ArrayList<>();
        for (int i = 0; i < arrayBids.size(); i++) {
            JSONArray item = arrayBids.getJSONArray(i);
            Level level = new Level(item.getBigDecimal(0), item.getBigDecimal(1));
            levelsBids.add(level);
        }
        List<Level> levelsAsks = new ArrayList<>();
        JSONArray arrayAsks = depth.getJSONArray("asks");
        for (int i = 0; i < arrayAsks.size(); i++) {
            JSONArray item = arrayAsks.getJSONArray(i);
            Level level = new Level(item.getBigDecimal(0), item.getBigDecimal(1));
            levelsAsks.add(level);
        }
        return new Depth(null, timestamp, levelsBids, levelsAsks);
    }

    /**
     * 序列化precision
     * @param jo json格式的precision数据
     * @return
     */
    public static Map<String, Precision> parsePrecisions(JSONObject jo) {
        if (jo == null) { return null; }
        JSONArray data = jo.getJSONArray("data");

        Map<String, Precision> precisionMap = new HashMap<>();
        if (data == null) { return null; }
        for (int i = 0; i < data.size(); i++) {
            JSONObject p = data.getJSONObject(i);
            String symbol = p.getString("base-currency") + "/" + p.getString("quote-currency");
            symbol = symbol.toUpperCase();
            Integer lPrecision = p.getInteger("amount-precision");
            Integer rPrecision = p.getInteger("price-precision");
            precisionMap.put(symbol, new Precision(null, symbol, lPrecision, rPrecision));
        }
        return precisionMap;
    }

    /**
     * 序列化accountinfo数据
     * @param jo json格式的accountinfo数据
     * @return accountId
     */
    public static String parseAccountInfo(JSONObject jo) {
        if (!"ok".equals(jo.getString("status"))) { return null; }
        JSONArray data = jo.getJSONArray("data");
        for (int i = 0; i < data.size(); i++) {
            JSONObject json = data.getJSONObject(i);
            if ("spot".equals(json.getString("type"))) {
                return json.getString("id");
            }
        }
        return null;
    }

    /**
     * 序列化account数据
     * @param symbol 币对
     * @param jo json格式的account数据
     * @return
     */
    public static Account parseAccount(String symbol, JSONObject jo) {
        BigDecimal lAvailable = BigDecimal.ZERO;
        BigDecimal lFrozen = BigDecimal.ZERO;
        BigDecimal rAvailable = BigDecimal.ZERO;
        BigDecimal rFrozen = BigDecimal.ZERO;

        String[] arg = symbol.toLowerCase().split("/");
        if (!"ok".equals(jo.getString("status"))) { return null; }
        JSONArray list = jo.getJSONObject("data").getJSONArray("list");
        for (int i = 0; i < list.size(); i++) {
            JSONObject json = list.getJSONObject(i);
            if (arg[0].equals(json.getString("currency"))) {
                if ("trade".equals(json.getString("type"))) {
                    lAvailable = json.getBigDecimal("balance");
                } else if("frozen".equals(json.getString("type"))) {
                    lFrozen = json.getBigDecimal("balance");
                }
            }
            if (arg[1].equals(json.getString("currency"))) {
                if ("trade".equals(json.getString("type"))) {
                    rAvailable = json.getBigDecimal("balance");
                } else if("frozen".equals(json.getString("type"))) {
                    rFrozen = json.getBigDecimal("balance");
                }
            }
        }
        return new Account(null, symbol,
                new Balance(null, arg[0], lAvailable, lFrozen),
                new Balance(null, arg[1], rAvailable, rFrozen));
    }


    /**
     * 解析Order
     * @param t 原始数据
     * @return Order
     */
    public static Order parseOrder(JSONObject t) {

        /*
            {
              "data":
              {
                "id": 59378,
                "symbol": "ethusdt",
                "account-id": 100009,
                "amount": "10.1000000000",
                "price": "100.1000000000",
                "created-at": 1494901162595,
                "type": "buy-limit",
                "field-amount": "10.1000000000",
                "field-cash-amount": "1011.0100000000",
                "field-fees": "0.0202000000",
                "finished-at": 1494901400468,
                "user-id": 1000,
                "source": "api",
                "state": "filled",
                "canceled-at": 0,
                "exchange": "huobi",
                "batch": ""
              }
            }
        */
//TODO 测试对象
//        JSONObject jsonObject = JSONObject.parseObject("              {\n" +
//                "                \"id\": 59378,\n" +
//                "                \"symbol\": \"ethusdt\",\n" +
//                "                \"account-id\": 100009,\n" +
//                "                \"amount\": \"10.1000000000\",\n" +
//                "                \"price\": \"100.1000000000\",\n" +
//                "                \"created-at\": 1494901162595,\n" +
//                "                \"type\": \"buy-limit\",\n" +
//                "                \"field-amount\": \"10.1000000000\",\n" +
//                "                \"field-cash-amount\": \"1011.0100000000\",\n" +
//                "                \"field-fees\": \"0.0202000000\",\n" +
//                "                \"finished-at\": 1494901400468,\n" +
//                "                \"user-id\": 1000,\n" +
//                "                \"source\": \"api\",\n" +
//                "                \"state\": \"filled\",\n" +
//                "                \"canceled-at\": 0,\n" +
//                "                \"exchange\": \"huobi\",\n" +
//                "                \"batch\": \"\"\n" +
//                "              }");
//
//        t = jsonObject ;

        Long time = t.getDate("created-at").getTime();
        String id = t.getString("id");
        Side side = null;
        Type type = null;
        switch (t.getString("type")){
            case "buy-market":
                side = Side.BUY;
                type = Type.MARKET;
            case "buy-limit":
                side = Side.BUY;
                type = Type.LIMIT;
            case "sell-market":
                side = Side.SELL;
                type = Type.MARKET;
            case "sell-limit":
                side = Side.SELL;
                type = Type.LIMIT;
        }

        BigDecimal price = t.getBigDecimal("price");
        BigDecimal quantity = t.getBigDecimal("amount");
        BigDecimal deal = t.getBigDecimal("field-amount");
        State state = null;
        switch (t.getString("state")) {
            case "submitting":
                state = State.SUBMIT;
                break;
            case "partial-filled":
                state = State.PARTIAL;
                break;
            case "filled":
                state = State.FILLED;
                break;
            case "cancelled":
                state = State.CANCEL;
                break;
            case "submitted":
                state = State.SUBMIT;
                break;
            default:
        }

        return new Order(null, time, id, price, quantity, deal, side, type, state);
    }

    /**
     * 统一解析orders
     * @param r json
     * @return orders
     */
    public static List<Order> parseOrders(JSONArray r) {
        List<Order> orders = new LinkedList<>();
        for (int i = 0; i< r.size(); i++) {
            orders.add(parseOrder(r.getJSONObject(i)));
        }
        return orders;
    }

    /**
     * 统一解析Trades
     */
    public static List<Trade> parseTrades(JSONArray jsonArray, String orderId) {
            /*
                {
                  "data": [
                    {
                      "id": 29553,
                      "order-id": 59378,
                      "match-id": 59335,
                      "symbol": "ethusdt",
                      "type": "buy-limit",
                      "source": "api",
                      "price": "100.1000000000",
                      "filled-amount": "9.1155000000",
                      "filled-fees": "0.0182310000",
                      "created-at": 1494901400435
                    }

                  ]
                }
             */

//TODO 测试对象
//            JSONArray  jsonArray1 = JSONArray.parseArray("[\n" +
//                    "                    {\n" +
//                    "                      \"id\": 29553,\n" +
//                    "                      \"order-id\": 59378,\n" +
//                    "                      \"match-id\": 59335,\n" +
//                    "                      \"symbol\": \"ethusdt\",\n" +
//                    "                      \"type\": \"buy-limit\",\n" +
//                    "                      \"source\": \"api\",\n" +
//                    "                      \"price\": \"100.1000000000\",\n" +
//                    "                      \"filled-amount\": \"9.1155000000\",\n" +
//                    "                      \"filled-fees\": \"0.0182310000\",\n" +
//                    "                      \"created-at\": 1494901400435\n" +
//                    "                    }\n" +
//                    "                   \n" +
//                    "                  ]");
//
//        jsonArray = jsonArray1;

        if(jsonArray == null){
            return null;
        }

        List<Trade> trades = new ArrayList<>();
        ListIterator<Object> iterator = jsonArray.listIterator();
        while (iterator.hasNext()) {
            JSONObject jsonObject = (JSONObject) iterator.next();
            if (jsonObject != null) {
                String jsonOrderId = jsonObject.getString("order_id");
                Long timestamp = jsonObject.getLong("created-at");
                String dealId = jsonObject.getString("id");
                BigDecimal price = jsonObject.getBigDecimal("price");
                BigDecimal quantity = jsonObject.getBigDecimal("filled-amount");
                Side side = null;
                switch (jsonObject.getString("type")){
                    case "buy-market":
                        side = Side.BUY;
                    case "buy-limit":
                        side = Side.BUY;
                    case "sell-market":
                        side = Side.SELL;
                    case "sell-limit":
                        side = Side.SELL;
                }
                trades.add(new Trade(jsonObject.toJSONString(), timestamp, jsonOrderId, dealId, price, quantity, null, null));
            }
        }
        return null;
    }

    /**
     * 将币对转换成交易所格式
     * @param symbol 币对
     * @return
     */
    public static String transfSymbol(String symbol) {
        return symbol.replace("/", "").toLowerCase();
    }


}
