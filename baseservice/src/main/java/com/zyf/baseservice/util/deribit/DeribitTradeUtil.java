package com.zyf.baseservice.util.deribit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyf.common.model.Balance;
import com.zyf.common.model.InstrumentTrade;
import com.zyf.common.model.Order;
import com.zyf.common.model.enums.*;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * deribit交易工具类
 *
 * @author yuanfeng.z
 * @date 2020/7/27 22:02
 */
@Slf4j
public class DeribitTradeUtil {

    /**
     * Balance序列化
     * @param jo json格式的Balance数据
     * @return
     */
    public static Balance parseBalance(JSONObject jo) {
        JSONObject j = jo.getJSONObject("result");
        String currency = j.getString("currency");
        BigDecimal available = j.getBigDecimal("available_funds");
        BigDecimal frozen = j.getBigDecimal("initial_margin");
        return new Balance(jo.toJSONString(), currency, available, frozen);
    }

    /**
     * Contract序列化
     * @param jo json格式的Contract数据
     * @param symbol 合约名
     * @return
     */
    public static BigDecimal parseContract(JSONObject jo, String symbol) {
        if (!check(jo)) {
            return null;
        }
        JSONArray result = jo.getJSONArray("result");
        if (Objects.nonNull(result) && result.size() != 0) {
            for (Object object : result) {
                JSONObject j = (JSONObject) object;
                if (j.getString("instrument").equals(symbol)) {
                    return j.getBigDecimal("amount");
                }
            }
            return BigDecimal.ZERO;
        } else {
            return BigDecimal.ZERO;
        }
    }

    /**
     * Order序列化
     * @param jo json格式的Order数据
     * @return
     */
    public static Order parseOrder(JSONObject jo) {
        JSONObject j = jo.getJSONObject("result");
        State state = null;
        if (j.getString("order_state").equals("open")) {
            state = State.SUBMIT;
        } else if (j.getString("order_state").equals("filled")) {
            state = State.FILLED;
        } else if (j.getString("order_state").equals("rejected")) {
            state = State.REJECTED;
        } else if (j.getString("order_state").equals("cancelled")) {
            state = State.CANCEL;
        }
        return new Order(
                null,
                j.getLong("creation_timestamp"),
                j.getString("order_id"),
                j.getBigDecimal("price"),
                j.getBigDecimal("amount"),
                j.getBigDecimal("filled_amount"),
                Side.valueOf(j.getString("direction").toUpperCase()),
                Type.valueOf(j.getString("order_type").toUpperCase()),
                state
        );
    }

    /**
     * Orders序列化
     * @param jo json格式的Orders数据
     * @return
     */
    public static List<Order> parseOrders(JSONObject jo) {
        JSONArray jb = jo.getJSONArray("result");
        if (Objects.nonNull(jb)) {
            if (jb.size() >= 0) {
                List<Order> orderList = new ArrayList<>();
                for (Object jsonobejct : jb) {
                    JSONObject j = (JSONObject) jsonobejct;
                    State state = null;
                    if (j.getString("order_state").equals("open")) {
                        state = State.SUBMIT;
                    } else if (j.getString("order_state").equals("filled")) {
                        state = State.FILLED;
                    } else if (j.getString("order_state").equals("rejected")) {
                        state = State.REJECTED;
                    } else if (j.getString("order_state").equals("cancelled")) {
                        state = State.CANCEL;
                    } else if (j.getString("order_state").equals("untriggered")) {
                        state = State.STOP;
                    }

                    orderList.add(new Order(
                            null,
                            j.getLong("creation_timestamp"),
                            j.getString("order_id"),
                            state.getValue().equals(State.STOP.getValue()) ? j.getBigDecimal("stop_price")
                                    : j.getBigDecimal("price"),
                            j.getBigDecimal("amount"),
                            j.getBigDecimal("filled_amount"),
                            Side.valueOf(j.getString("direction").toUpperCase()),
                            Type.valueOf(j.getString("order_type").toUpperCase()),
                            state
                    ));
                }
                return orderList;
            }
        }
        return null;
    }

    /**
     * 获取未成交订单
     *
     * @param jo
     * @return
     */
    public static List<Order> unsettledOrders(JSONObject jo) {
        JSONArray jb = jo.getJSONArray("result");
        if (Objects.nonNull(jb)) {
            if (jb.size() != 0) {
                List<Order> orderList = new ArrayList<>();
                for (Object jsonobejct : jb) {
                    JSONObject j = (JSONObject) jsonobejct;

                    State state = null;
                    if (j.getString("order_state").equals("open")) {
                        state = State.SUBMIT;
                    } else if (j.getString("order_state").equals("filled")) {
                        state = State.FILLED;
                    } else if (j.getString("order_state").equals("rejected")) {
                        state = State.REJECTED;
                    } else if (j.getString("order_state").equals("cancelled")) {
                        state = State.CANCEL;
                    }


                    orderList.add(new Order(
                            null,
                            j.getLong("creation_timestamp"),
                            j.getString("order_id"),
                            j.getBigDecimal("price"),
                            j.getBigDecimal("amount"),
                            j.getBigDecimal("filled_amount"),
                            Side.valueOf(j.getString("direction").toUpperCase()),
                            Type.valueOf(j.getString("order_type").toUpperCase()),
                            state
                    ));
                }
                return orderList;
            } else {
                return new ArrayList<>();
            }
        }
        return null;
    }

    /**
     * InstrumentTrade序列化
     *
     * @param jo json格式的InstrumentTrade数据
     * @return
     */
    public static List<InstrumentTrade> parseInstrumentTrade(JSONObject jo) {
        JSONObject jsonObject = jo.getJSONObject("result");
        if (jsonObject == null) {
            return null;
        }
        JSONArray trades = jsonObject.getJSONArray("trades");
        List<InstrumentTrade> t = new ArrayList<>();
        for (Object j : trades) {
            JSONObject js = (JSONObject) j;
            InstrumentTrade instrumentTrade = new InstrumentTrade();
            instrumentTrade.setId(js.getLong("order_id"));
            instrumentTrade.setFee(js.getBigDecimal("fee"));
            instrumentTrade.setPrice(js.getBigDecimal("price"));
            instrumentTrade.setContract(js.getBigDecimal("amount"));
            instrumentTrade.setCoinSymbol(js.getString("fee_currency"));
            instrumentTrade.setPair(js.getString("instrument_name"));
            instrumentTrade.setSide(js.getString("direction") == "sell" ? Side.SELL : Side.BUY);
            instrumentTrade.setType("limit".equals(js.getString("order_type")) ? Type.LIMIT : "market".equals(js.getString("order_type")) ? Type.MARKET : Type.LIQUIDATION);
            instrumentTrade.setRole("M".equals(js.getString("liquidity")) ? Role.MAKER : Role.TAKER);
            instrumentTrade.setDate(new Date(js.getLong("timestamp")));
            t.add(instrumentTrade);
        }
        return t;
    }

    /**
     * 获取下单返回值orderid
     *
     * @param jo json格式的buyOrSellLimitOrMarket数据
     * @return
     */
    public static String parsebuyOrSellLimitOrMarket(JSONObject jo) {
        JSONObject jsonObject = jo.getJSONObject("result");
        if (Objects.nonNull(jsonObject)) {
            return jsonObject.getJSONObject("order").getString("order_id");
        }
        return null;
    }

    public static List<Order> parsePosition(JSONObject jo) {
        return null;
    }

    /**
     * 检测交易所返回数据
     * @param jo 交易所返回数据
     * @return
     */
    private static Boolean check(JSONObject jo) {
        if (Objects.nonNull(jo.getString("error"))) {
            log.error("deribit: error:" + jo.getString("error"));

            if (Objects.nonNull(jo.getString("message"))) {
                log.error("deribit: error:" + jo.getString("message"));
            }
            return false;
        }
        return true;
    }

}
