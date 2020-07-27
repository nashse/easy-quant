package com.zyf.baseservice.util.deribit;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyf.common.model.Depth;
import com.zyf.common.model.Level;
import com.zyf.common.model.Precision;
import com.zyf.common.model.Ticker;
import com.zyf.common.model.future.Instrument;
import com.zyf.common.model.future.Instrument.Type;

import java.math.BigDecimal;
import java.util.*;

/**
 * deribit工具类
 *
 * @author yuanfeng.z
 * @date 2020/7/27 17:07
 */
public class DeribitUtil {

    /**
     * 字符编码
     */
    public final static String INPUT_CHARSET = "UTF-8";

    /**
     * 序列化ticker
     *
     * @param jo json格式的ticker数据
     * @return
     */
    public static Ticker parseTicker(JSONObject jo) {
        if (jo == null) {
            return null;
        }
        ;
        JSONObject result = jo.getJSONObject("result");
        Long timestamp = result.getLong("timestamp");
        BigDecimal close = result.getBigDecimal("last_price");

        JSONObject stats = result.getJSONObject("stats");
        BigDecimal high = stats.getBigDecimal("high");
        BigDecimal low = stats.getBigDecimal("low");
        BigDecimal vol = stats.getBigDecimal("volume");
        return new Ticker(null, timestamp, null, high, low, close, vol);
    }

    /**
     * 序列化Depth
     *
     * @param jo json格式的Depth数据
     * @return
     */
    public static Depth parseDepth(JSONObject jo) {
        if (jo == null) {
            return null;
        }
        JSONObject result = jo.getJSONObject("result");
        Long timestamp = result.getLong("timestamp");
        List<Level> asks = new ArrayList<>();
        List<Level> bids = new ArrayList<>();
        JSONArray Jbids = result.getJSONArray("bids");
        for (int i = 0; i < Jbids.size(); i++) {
            bids.add(new Level(Jbids.getJSONArray(i).getBigDecimal(0), Jbids.getJSONArray(i).getBigDecimal(1)));
        }
        JSONArray Jasks = result.getJSONArray("asks");
        for (int i = 0; i < Jasks.size(); i++) {
            asks.add(new Level(Jasks.getJSONArray(i).getBigDecimal(0), Jasks.getJSONArray(i).getBigDecimal(1)));
        }
        asks.sort(Comparator.comparing(Level::getPrice));
        bids.sort((o1, o2) -> o2.getPrice().compareTo(o1.getPrice()));
        return new Depth(null, timestamp, bids, asks);
    }

    public static Map<String, Precision> parseCurrencyPrecisions(JSONObject jo) {
        if (Objects.nonNull(jo)) {
            Map<String, Precision> map_Currency_Precision = new HashMap<>();
            JSONArray result = jo.getJSONArray("result");
            if (0 < result.size()) {
                for (Object jsonobejct : result) {
                    JSONObject j = (JSONObject) jsonobejct;
                    String symbol = j.getString("symbol");
                    String mt = j.getString("min_trade_amount");
                    String Precision;
                    if (0 < mt.indexOf(".")) {
                        Precision = mt.substring((mt.indexOf(".") + 1), mt.length());
                        map_Currency_Precision.put(j.getString("instrument_name"), new Precision(null, j.getString("instrument_name"), Precision.length(), null));
                    }
                }
                return map_Currency_Precision;
            }
        }
        return null;
    }

    public static List<Instrument> parseCurrencyInstrument(JSONObject jo) {
        if (Objects.nonNull(jo)) {
            List<Instrument> list = new ArrayList<>();
            JSONArray result = jo.getJSONArray("result");
            if (0 < result.size()) {
                for (Object jsonobejct : result) {
                    JSONObject j = (JSONObject) jsonobejct;
                    list.add(
                            new Instrument(
                                    j.getString("instrument_name"),
                                    Type.OPTION,
                                    j.getString("base_currency"),
                                    null,
                                    j.getBigDecimal("min_trade_amount"),
                                    null,
                                    j.getBigDecimal("tick_size"),
                                    j.getBoolean("is_active"),
                                    Instrument.Settle.valueOf(j.getString("settlement_period").toUpperCase()),
                                    j.getLong("creation_timestamp"),
                                    j.getLong("expiration_timestamp"))
                    );

                }
                return list;
            }
        }
        return null;
    }

    /**
     * 将币对转换成交易所格式
     *
     * @param symbol 币对
     * @return
     */
    public static String transfSymbol(String symbol) {
        return symbol.replace("/", "");
    }
}

