package com.zyf.baseservice.util.huobipro;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyf.common.model.*;
import com.zyf.common.model.enums.Side;
import com.zyf.common.model.enums.State;
import com.zyf.common.model.enums.Type;
import com.zyf.common.model.future.InstrumentFut;
import com.zyf.common.model.option.Instrument;
import com.zyf.common.model.option.OptionDetailInfo;
import com.zyf.common.util.DateUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * 火币qiquan工具类
 *
 * @author yuanfeng.z
 * @date 2020/7/29 21:07
 */
@Slf4j
public class HuobiProOptionUtil {

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
     * 序列化contractInfo
     *
     * @param jo json格式的contractInfo数据
     * @return
     */
    public static List<Instrument> parseContractInfo(JSONObject jo) {
        if (jo == null) {
            return null;
        }
        Long timestamp = jo.getLong("ts");
        JSONArray data = jo.getJSONArray("data");
        if (data == null) {
            return null;
        }

        List<Instrument> instruments = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject j = data.getJSONObject(i);
//            String originData, String name, Type type, String baseCurrency, String countCurrency,
//                    BigDecimal minTradeSize, Integer countPrecision, BigDecimal tickSize, Boolean active,
//                    BigDecimal iv, Settle settle, BigDecimal strike, Long created, Long expired
            instruments.add(
                    new Instrument(
                            j.toJSONString(),
                            j.getString("contract_code"),
                            Instrument.Type.OPTION,
                            j.getString("symbol"),
                            null,
                            null,
                            null,
                            j.getBigDecimal("price_tick"),
                            null,
                            null,
                            Instrument.Settle.valueOf(HuobiProOptionUtil.tansfDate(j.getString("contract_type"))),
                            j.getBigDecimal("exercise_price"),
                            DateUtil.str2TimeStamp(j.getString("create_date"), DateUtil.yyyyMMdd),
                            DateUtil.str2TimeStamp(j.getString("delivery_date"), DateUtil.yyyyMMdd))
            );
        }
        return instruments;
    }

    /**
     * 序列化optionDetailInfo
     *
     * @param jo json格式的optionDetailInfo数据
     * @return
     */
    public static List<OptionDetailInfo> parseOptionDetailInfo(JSONObject jo) {
        if (jo == null) {
            return null;
        }
        Long timestamp = jo.getLong("ts");
        JSONArray data = jo.getJSONArray("data");
        if (data == null) {
            return null;
        }

        /**
         * {
         *   "status": "ok",
         *   "data": [
         *     {
         *       "symbol": "BTC",
         *       "trade_partition": "USDT",
         *       "contract_type": "quarter",
         *       "option_right_type": "C",
         *       "contract_code": "BTC-USDT-200508-C-8800",
         *       "iv_last_price": 175.05,
         *       "iv_ask_one": 118.93,
         *       "iv_bid_one": 57.32,
         *       "iv_mark_price": 84.41,
         *       "delta": 0.2,
         *       "gamma": 0.11,
         *       "theta": 0.23,
         *       "vega": 0.55,
         *       "ask_one": 1.55,
         *       "bid_one": 1.2,
         *       "last_price": 1.11,
         *       "mark_price": 1.23
         *     }
         *   ],
         *   "ts": 1590018789304
         * }
         */

        List<OptionDetailInfo> optionDetailInfos = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            JSONObject j = data.getJSONObject(i);
            optionDetailInfos.add(
                    new OptionDetailInfo(
                            null,
                            j.getString("symbol"),
                            j.getString("trade_partition"),
                            j.getString("contract_type"),
                            j.getString("option_right_type"),
                            j.getString("contract_code"),
                            j.getBigDecimal("iv_last_price"),
                            j.getBigDecimal("iv_ask_one"),
                            j.getBigDecimal("iv_bid_one"),
                            j.getBigDecimal("iv_mark_price"),
                            j.getBigDecimal("delta"),
                            j.getBigDecimal("gamma"),
                            j.getBigDecimal("theta"),
                            j.getBigDecimal("vega"),
                            j.getBigDecimal("ask_one"),
                            j.getBigDecimal("bid_one"),
                            j.getBigDecimal("last_price"),
                            j.getBigDecimal("mark_price"))
            );
        }
        return optionDetailInfos;
    }

    /**
     * 解析orders
     * @param jo 原始数据
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
     * 解析Order
     *
     * @param jo 原始数据
     * @return Order
     */
    public static Order parseOrder(JSONObject jo) {
        Long time = jo.getDate("create_date").getTime();
        String id = jo.getString("order_id");
        Type type = null;
        Side side = null;
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

        BigDecimal price = jo.getBigDecimal("price");
        BigDecimal quantity = jo.getBigDecimal("volume");
        BigDecimal deal = null;
        State state = null;

        return new Order(jo.toJSONString(), time, id, price, quantity, deal, side, type, state);
    }

    /**
     * 解析trades
     * @param jo 原始数据
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
     * 解析trade
     *
     * @param jo 原始数据
     * @return Order
     */
    public static Trade parseTrade(JSONObject jo, String orderId) {
        if (Objects.isNull(jo)) {
            return null;
        }
        Long time = jo.getDate("created_at").getTime();
        String dealId = jo.getString("trade_id");
        BigDecimal fee = jo.getBigDecimal("trade_fee");
        String feeAsset = jo.getString("fee_asset");

        BigDecimal price = jo.getBigDecimal("trade_price");
        BigDecimal deal = jo.getBigDecimal("trade_volume");
        return new Trade(null, time, orderId, dealId, price, deal, fee, feeAsset);
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

    /**
     * 转换周期
     * @param str
     * @return
     */
    private static String tansfDate(String str) {
        if ("this_week".equals(str)) {
            return str.split("_")[1].toUpperCase();
        } else if("quarter".equals(str)) {
            return str.toUpperCase();
        } else if ("next_week".equals(str)) {
            return str.toUpperCase();
        }
        return null;
    }
}
