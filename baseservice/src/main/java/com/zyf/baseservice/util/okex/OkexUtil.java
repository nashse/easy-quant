package com.zyf.baseservice.util.okex;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyf.common.model.*;
import com.zyf.common.model.enums.Side;
import com.zyf.common.model.enums.State;
import com.zyf.common.model.enums.Type;

import java.math.BigDecimal;
import java.util.*;

import static java.math.BigDecimal.ZERO;

/**
 * okex工具类
 * @author yuanfeng.z
 * @date 2019/6/27 11:32
 */
public class OkexUtil {

    /**
     * 字符编码
     */
    public final static String INPUT_CHARSET = "UTF-8";

    /**
     * 序列化ticker
     * @param jo json格式的ticker数据
     * @return
     */
    public static Ticker parseTicker(JSONObject jo) {
        if (jo == null) { return null; };
        Long timestamp = jo.getLong("timestamp");
        BigDecimal open = jo.getBigDecimal("open_24h");
        BigDecimal close = jo.getBigDecimal("last");
        BigDecimal high = jo.getBigDecimal("high_24h");
        BigDecimal low = jo.getBigDecimal("low_24h");
        BigDecimal vol = jo.getBigDecimal("base_volume_24h");
        return new Ticker(null, timestamp, open, high, low, close, vol);
    }

    public static Depth parseDepth(JSONObject jo) {
        if (jo == null) { return null; };
        Long timestamp = jo.getLong("timestamp");
        JSONArray arrayBids = jo.getJSONArray("bids");
        List<Level> levelsBids = new ArrayList<>();
        for (int i = 0; i < arrayBids.size(); i++) {
            JSONArray item = arrayBids.getJSONArray(i);
            Level level = new Level(item.getBigDecimal(0), item.getBigDecimal(1));
            levelsBids.add(level);
        }
        List<Level> levelsAsks = new ArrayList<>();
        JSONArray arrayAsks = jo.getJSONArray("asks");
        for (int i = 0; i < arrayAsks.size(); i++) {
            JSONArray item = arrayAsks.getJSONArray(i);
            Level level = new Level(item.getBigDecimal(0), item.getBigDecimal(1));
            levelsAsks.add(level);
        }
        return new Depth(null, timestamp, levelsBids, levelsAsks);
    }

    public static Map<String, Precision> parsePrecisions(JSONArray jo) {
        if (jo.isEmpty()) { return null; }
        Map<String, Precision> precisionMap = new HashMap<>();
        for (int i = 0; i < jo.size(); i++) {
            JSONObject p = jo.getJSONObject(i);
            String symbol = p.getString("base_currency") + "/" + p.getString("quote_currency");
            BigDecimal sizeIncrement = p.getBigDecimal("size_increment");
            Integer lPrecision = sizeIncrement.scale();
            BigDecimal tickSize = p.getBigDecimal("tick_size");
            Integer rPrecision = tickSize.scale();
            precisionMap.put(symbol, new Precision(null, symbol, lPrecision, rPrecision));
        }
        return precisionMap;
    }

    /**
     * 将币对转换成交易所格式
     * @param symbol 币对
     * @return
     */
    public static String transfSymbol(String symbol) {
        return symbol.replace("/", "-");
    }


    /**
     * 将币对转换成交易所格式 小写
     * @param symbol 币对
     * @return
     */
    public static String transfSymbolToLower(String symbol) {
        return symbol.replace("/", "-").toLowerCase();
    }

    /**
     * 将币对转换成两个单独的货币
     * @param symbol 币对
     * @return
     */
    public static String[] transfSymbolToArr(String symbol) {
        return symbol.toLowerCase().split("/");
    }

    /**
     * 序列化account数据
     * @param symbol 币对
     * @param l json格式的balance数据
     * @param r json格式的balance数据
     * @return
     */
    public static Account parseAccount(String symbol, JSONObject l, JSONObject r) {

        BigDecimal lAvailable = BigDecimal.ZERO;
        BigDecimal lFrozen = BigDecimal.ZERO;
        BigDecimal rAvailable = BigDecimal.ZERO;
        BigDecimal rFrozen = BigDecimal.ZERO;

        String[] arg = symbol.toLowerCase().split("/");

        if (l == null || r == null) { return null; }


        lAvailable = l.getBigDecimal("available");
        lFrozen = l.getBigDecimal("frozen");
        rAvailable = r.getBigDecimal("available");
        rFrozen = r.getBigDecimal("available");

        return new Account(null, symbol,
                new Balance(null, l.getString("currency"), lAvailable, lFrozen),
                new Balance(null, r.getString("currency"), rAvailable, rFrozen));

    }

    /**
     * 拼接参数
     *
     * @param paramMap
     */
    public static String addParams(LinkedHashMap<String, Object> paramMap) {
        String rtn = "{";
        for (Map.Entry<String, Object> pair : paramMap.entrySet()){
            String key = OkexSignUtil.urlEncoded(pair.getKey(), INPUT_CHARSET);
            String value = OkexSignUtil.urlEncoded(pair.getValue().toString(), INPUT_CHARSET);
            rtn += "'" + key + "'" + ":" + "'" + value + "'" + ",";
        }
        rtn = rtn.substring(0, rtn.length()-1)+ "}";
        return rtn;
    }


    /**
     * 拼接参数
     *
     * @param paramMap
     */
    public static String addParamsHK(LinkedHashMap<String, Object> paramMap) {
        String rtn = "{";
        for (Map.Entry<String, Object> pair : paramMap.entrySet()){
            String key = OkexSignUtil.urlEncoded(pair.getKey(), INPUT_CHARSET);

            if("tokens".equals(key)){
                List<String> valueList = (List) pair.getValue();
                String value = "";
                for (int x = 0; x<valueList.size();  x++) {

                    if (x != valueList.size() - 1){
                        value += '"' + valueList.get(x) + '"' + ",";
                    }else {
                        value += '"' + valueList.get(x) + '"';
                    }
                }
                rtn += '"' + key + '"' + ":" + '[' + value + ']' + ",";
                continue;
            }

            String value = pair.getValue().toString();
            rtn += '"' + key + '"' + ":" + '"' + value + '"' + ",";
        }
        rtn = rtn.substring(0, rtn.length()-1)+ "}";
        return rtn;
    }


    /**
     * 拼接参数
     *
     * @param
     */
    public static String addParamsList(String symbol, List<String> ids) {

        StringBuilder stringBuilder = new StringBuilder();
        return stringBuilder.append("[{").append("\"instrument_id\"")
                .append(":").append('"' + symbol + '"').append(",")
                .append("\"order_ids\"").append(":" + ids.toString())
                .append("}]").toString();
    }

    /**
     * 解析Order
     * @param t 原始数据
     * @return Order
     */
    public static Order parseOrder(JSONObject t) {

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

        Long time = t.getDate("timestamp").getTime();
        String id = t.getString("order_id");
        Side side = Side.valueOf(t.getString("side").toUpperCase());
        Type type = Type.valueOf(t.getString("type").toUpperCase());
        BigDecimal price = t.getBigDecimal("price");
        BigDecimal quantity = t.getBigDecimal("size");
        BigDecimal deal = t.getBigDecimal("filled_size");
        State state = null;
        switch (t.getString("status")) {
            case "open":
                state = State.SUBMIT;
                break;
            case "part_filled":
                state = State.PARTIAL;
                break;
            case "canceling":
                state = State.CANCEL;
                break;
            case "filled":
                state = State.FILLED;
                break;
            case "cancelled":
                state = State.CANCEL;
                break;
            case "failure":
                state = State.CANCEL;
                break;
            case "ordering":
                state = State.SUBMIT;
                break;
            default:
        }
        BigDecimal amount = t.getBigDecimal("size");
        BigDecimal average = ZERO;

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
             * [
                  {
                    "created_at":"2019-03-15T02:52:56.000Z",
                    "exec_type":"T",
                    "fee":"0",
                    "instrument_id":"BTC-USDT",
                    "ledger_id":"3963052722",
                    "liquidity":"T",
                    "order_id":"2482659399697408",
                    "price":"3888.6",
                    "product_id":"BTC-USDT",
                    "side":"buy",
                    "size":"0.00044694",
                    "timestamp":"2019-03-15T02:52:56.000Z"
                  },
                  {
                    "before":"3963052722",
                    "after":"3963052718"
                   }
             * ]
             */
        List<Trade> trades = new ArrayList<>();
        ListIterator<Object> iterator = jsonArray.listIterator();
        while (iterator.hasNext()) {
            JSONObject jsonObject = (JSONObject) iterator.next();
            if (jsonObject != null) {
                String jsonOrderId = jsonObject.getString("order_id");
                if (orderId.equals(jsonOrderId)) {
                    Long timestamp = jsonObject.getLong("timestamp");
                    String dealId = jsonObject.getString("ledger_id");
                    BigDecimal price = jsonObject.getBigDecimal("price");
                    BigDecimal quantity = jsonObject.getBigDecimal("size");
                    Side side = "sell".equals(jsonObject.getString("exec_type")) ? Side.SELL : Side.BUY;
                    trades.add(new Trade(jsonObject.toJSONString(), timestamp, jsonOrderId, dealId, price, quantity, null, null));
                }
            }
        }
        return trades;
    }


}
