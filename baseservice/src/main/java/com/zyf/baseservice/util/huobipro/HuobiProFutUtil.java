package com.zyf.baseservice.util.huobipro;

import com.zyf.common.model.*;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyf.common.model.enums.Side;
import com.zyf.common.model.enums.State;
import com.zyf.common.model.enums.Type;
import com.zyf.common.model.future.InstrumentFut;
import com.zyf.common.model.option.Instrument;
import com.zyf.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.*;

/**
 * 火币期货工具类
 *
 * @author yuanfeng.z
 * @date 2020/7/29 21:07
 */
@Slf4j
public class HuobiProFutUtil {

    /**
     * 将期货币对转换成交易所格式
     *
     * @param symbol 币对
     * @return
     */
    public static String transfSymbol(String symbol) {
        return symbol.replace("/", "-");
    }

    /**
     * 序列化depth
     *
     * @param jo json格式的depth数据
     * @return
     */
    public static Depth parseDepth(JSONObject jo) {
        if (jo == null) {
            return null;
        }
        Long timestamp = jo.getLong("ts");
        JSONObject depth = jo.getJSONObject("tick");
        if (depth == null) {
            return null;
        }
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
     * 序列化kline
     *
     * @param jo json格式的kline数据
     * @return
     */
    public static List<Kline> parseKline(JSONObject jo) {
        if (jo == null) {
            return null;
        }
        String status = jo.getString("status");
        if (!"ok".equals(status)) {
            return null;
        }

        JSONArray klines = jo.getJSONArray("data");
        if (klines == null) {
            return null;
        }

        List<Kline> klineList = new ArrayList<>();
        for (int i =0; i < klines.size(); i++) {
            Kline kline = new Kline(klines.getJSONObject(i).toJSONString(),
                    null,
                    klines.getJSONObject(i).getBigDecimal("open"),
                    klines.getJSONObject(i).getBigDecimal("high"),
                    klines.getJSONObject(i).getBigDecimal("low"),
                    klines.getJSONObject(i).getBigDecimal("close"),
                    klines.getJSONObject(i).getBigDecimal("vol"));
            klineList.add(kline);
        }
        return klineList;
    }


    /**
     * 序列化deliveryContracts
     *
     * @param jo json格式的deliveryContracts数据
     * @return
     */
    public static List<InstrumentFut> parseDeliveryContracts(JSONObject jo) {
        if (jo == null) {
            return null;
        }
        Long timestamp = jo.getLong("ts");
        JSONArray data = jo.getJSONArray("data");
        if (data == null) {
            return null;
        }

        List<InstrumentFut> instruments = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject j = data.getJSONObject(i);
            instruments.add(new InstrumentFut(
                    null,
                    j.getString("contract_code"),
                    j.getString("symbol"),
                    null,
                    j.getBigDecimal("price_tick"),
                    null,
                    j.getString("contract_type"),
                    DateUtil.str2TimeStamp(j.getString("create_date"), DateUtil.yyyyMMdd),
                    DateUtil.str2TimeStamp(j.getString("delivery_date"), DateUtil.yyyyMMdd)));
        }
        return instruments;
    }

    /**
     * 解析Order
     *
     * @param jo 原始数据
     * @return Order
     */
    public static Order parseOrder(JSONObject jo) {

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
        Long time = jo.getDate("create_date").getTime();
        String id = jo.getString("order_id");
        Side side = null;
        Type type = null;
        switch (jo.getString("direction")) {
            case "buy":
                side = Side.BUY;
                break;
            case "sell":
                side = Side.SELL;
                break;
            default:
                side = null;
        }

        BigDecimal price =
                jo.getBigDecimal("trigger_price") != null
                ? jo.getBigDecimal("trigger_price")
                : jo.getBigDecimal("price");
        BigDecimal quantity = jo.getBigDecimal("volume");
        BigDecimal deal = null;
        State state = null;

        return new Order(jo.toJSONString(), time, id, price, quantity, deal, side, type, state);
    }

    /**
     * 序列化position
     *
     * @param jo json格式的position数据
     * @return
     */
    public static List<Position> parsePosition(JSONObject jo) {
        return null;
    }

    /**
     * 序列化pendingOrders
     *
     * @param jo json格式的pendingOrders数据
     * @return
     */
    public static List<Order> parsePendingOrders(JSONObject jo) {
        if (Objects.isNull(jo)) {
            return null;
        }
        JSONArray array = jo.getJSONObject("data").getJSONArray("orders");
        List<Order> orders = new LinkedList<>();
        for (int i = 0; i < array.size(); i++) {
            orders.add(parseOrder(array.getJSONObject(i)));
        }
        return orders;
    }

    /**
     * 序列化account
     *
     * @param instrument 合约名
     * @param jo         json格式的account数据
     * @return
     */
    public static Account parseAccount(String instrument, JSONObject jo) {
        BigDecimal lAvailable = BigDecimal.ZERO;
        BigDecimal lFrozen = BigDecimal.ZERO;
        BigDecimal rAvailable = BigDecimal.ZERO;
        BigDecimal rFrozen = BigDecimal.ZERO;

        String[] arg = instrument.split("/");
        if (!"ok".equals(jo.getString("status"))) {
            return null;
        }
        JSONArray list = jo.getJSONArray("data");
        for (int i = 0; i < list.size(); i++) {
            JSONObject json = list.getJSONObject(i);
            if (arg[0].equals(json.getString("symbol"))) {
                lAvailable = json.getBigDecimal("margin_available");
                lFrozen = json.getBigDecimal("margin_frozen");
            }
            if (arg[1].equals(json.getString("symbol"))) {
                rAvailable = json.getBigDecimal("margin_available");
                rFrozen = json.getBigDecimal("margin_frozen");
            }
        }
        return new Account(null, instrument,
                new Balance(null, arg[0], lAvailable, lFrozen),
                new Balance(null, arg[1], rAvailable, rFrozen));
    }

    /**
     * 序列化歷史订单
     *
     * @param jo json格式的orders数据
     * @return
     */
    public static List<Order> parseOrders(JSONObject jo) {
        if (Objects.isNull(jo) ||
                Objects.isNull(jo.getJSONObject("data"))) {
            return null;
        }

        List<Order> orders = new ArrayList<>();
        JSONArray data = jo.getJSONObject("data").getJSONArray("orders");
        for (int i = 0; i < data.size(); i++) {
            JSONObject json = data.getJSONObject(i);
            Order order = parseOrder(json);
            orders.add(order);
        }
        return orders;
    }

    /**
     * 序列化成交明細
     *
     * @param jo json格式的trades数据
     * @return
     */
    public static List<Trade> parseTrades(JSONObject jo) {
        if (Objects.isNull(jo) ||
                Objects.isNull(jo.getJSONObject("data"))) {
            return null;
        }

        List<Trade> trades = new ArrayList<>();
        JSONArray data = jo.getJSONObject("data").getJSONArray("trades");
        for (int i = 0; i < data.size(); i++) {
            JSONObject json = data.getJSONObject(i);
            Long time = json.getDate("created_at").getTime();
            String dealId = new StringBuilder(json.getString("id"))
                    .append("/")
                    .append(json.getString("trade_id")).toString();
            BigDecimal fee = json.getBigDecimal("trade_fee");
            String feeAsset = json.getString("fee_asset");
            BigDecimal price = json.getBigDecimal("trade_price");
            BigDecimal deal = json.getBigDecimal("trade_volume");
            String orderId = jo.getJSONObject("data").getString("order_id_str");
            trades.add(new Trade(jo.toJSONString(), time, orderId, dealId, price, deal, fee, feeAsset));
        }
        return trades;
    }

    /**
     * 序列化cancelAll
     *
     * @param jo json格式的cancelAll数据
     * @return
     */
    public static Boolean parseCancelAll(JSONObject jo) {
        if (jo.getString("status").equals("error")) {
            if (jo.getInteger("err_code").equals(1051)) {
                log.info("err_msg -> No orders to cancel.");
                return true;
            }
            return false;
        }

        return true;
    }
}
