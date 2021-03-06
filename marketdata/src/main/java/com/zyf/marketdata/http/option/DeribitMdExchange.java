package com.zyf.marketdata.http.option;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.IMdExchange;
import com.zyf.baseservice.util.deribit.DeribitUtil;
import com.zyf.common.model.Depth;
import com.zyf.common.model.Kline;
import com.zyf.common.model.Precision;
import com.zyf.common.model.Ticker;
import com.zyf.common.model.option.Instrument;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import lombok.extern.slf4j.Slf4j;

/**
 * Deribit交易所实现类
 *
 * @author yuanfeng.z
 * @date 2019/6/27 10:02
 */
@Slf4j
public class DeribitMdExchange implements IMdExchange {

    /**
     * 协议
     */
    private static final String PROTOCOL = "https://";

    /**
     * 网站
     */
    private static final String SITE = "www.deribit.com";

    /**
     * URL
     */
    private static final String URL = PROTOCOL + SITE;

    /**
     * ticker资源路径
     */
    private static final String TICKEER = "/api/v2/public/ticker";

    /**
     * depth资源路径
     */
    private static final String DEPTH = "/api/v2/public/get_order_book";

    /**
     * 精密度
     */
    private static final String PRECISION = "/api/v2/api/v1/instrument";

    /**
     * 合约
     */
    private static final String INSTRUMENTS = "/api/v2/public/get_instruments";

    /**
     * 合约
     */
    private static final String LAST_TRADES = "/api/v2//public/get_last_trades_by_currency";

    /**
     * 币对
     */
    private static final String CURRENCIES = "/api/v2/public/get_currencies";

    /**
     * 单例模式
     *
     * @return
     */
    public static DeribitMdExchange deribitMdExchange = new DeribitMdExchange();

    public static DeribitMdExchange getInstance() {
        return deribitMdExchange;
    }

    private DeribitMdExchange() {
        super();
    }

    /**
     * 获取ticker
     *
     * @param symbol 合约名
     * @return
     */
    @Override
    public Ticker getTicker(String symbol) {
        // 转换币对格式
        symbol = DeribitUtil.transfSymbol(symbol);

        /*获取ticker数据*/
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(TICKEER).append("?").append("instrument_name=").append(symbol);
        String tickerStr = OkHttpV3ClientProxy.get(sb.toString());

        /*适配*/
        JSONObject jo = JSONObject.parseObject(tickerStr);
        return DeribitUtil.parseTicker(jo);
    }

    @Override
    public List<Kline> getKline(String symbol, String granularity) {
        return null;
    }

    /**
     * 获取depth
     *
     * @param symbol 合约名
     * @return
     */
    @Override
    public Depth getDepth(String symbol) {
        // 转换币对格式
        symbol = DeribitUtil.transfSymbol(symbol);

        /*获取depth数据*/
        StringBuilder sb = new StringBuilder();
        final int size = 5;
        sb.append(URL).append(DEPTH).append("?")
                .append("instrument_name=").append(symbol)
                .append("&depth=").append(size);
        String depthStr = null;
        try {
            depthStr = OkHttpV3ClientProxy.get(sb.toString());
        } catch (Exception e) {
            log.error("获取deribit盘口失败，错误异常:" + e.getMessage());
            return null;
        }
        /*适配*/
        JSONObject array = JSONObject.parseObject(depthStr);
        //return depth;
        return DeribitUtil.parseDepth(array);
    }

    /**
     * 获取精度
     *
     * @return
     */
    @Override
    public Map<String, Precision> getPrecisions() {
        return null;
    }


    public static List<Instrument> getCurrencyInstrument(String currency) {
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(INSTRUMENTS).append("?")
                .append("currency=").append(currency)
                .append("&expired=").append("false")
                .append("&kind=").append("option");
        String result = OkHttpV3ClientProxy.get(sb.toString());
        JSONObject jo = JSONObject.parseObject(result);
        return DeribitUtil.parseCurrencyInstrument(jo);
    }

    public static List<Instrument> getLastTradesByCurrency(String currency) {
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(LAST_TRADES).append("?")
                .append("currency=").append(currency)
                .append("&kind=").append("option");
        String result = OkHttpV3ClientProxy.get(sb.toString());
        JSONObject jo = JSONObject.parseObject(result);
        return DeribitUtil.parseLastTradesByCurrency(jo);
    }

    /**
     * 获取币对列表
     * @return
     */
    public List<String> getCurrencys() {
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(CURRENCIES);
        String result = OkHttpV3ClientProxy.get(sb.toString());
        JSONObject jo = JSONObject.parseObject(result);
        JSONArray fuck = jo.getJSONArray("result");
        List<String> list = new ArrayList<>();
        for (Object jsonobejct : fuck) {
            JSONObject j = (JSONObject) jsonobejct;
            list.add(j.getString("currency"));
        }
        return list;
    }

    public static void main(String[] args) {
        log.info(DeribitMdExchange.getInstance().getDepth("ETH-PERPETUAL").toString());
        List<Instrument> list = DeribitMdExchange.getCurrencyInstrument("BTC");
        for (Instrument i : list) {
            if (!i.getActive()) {
                log.info(i.toString());
                log.info("" + list.size());
            }
            Depth depth = DeribitMdExchange.getInstance().getDepth(i.getName());
//            log.info(depth.toString());
        }
        List<Instrument> lk = DeribitMdExchange.getLastTradesByCurrency("BTC");
        for (Instrument i : lk) {
            log.info(i.toString());
            log.info("" + list.size());
            if (!i.getActive()) {
                log.info(i.toString());
                log.info("" + list.size());
            }
//            Depth depth = DeribitMdExchange.getInstance().getDepth(i.getName());
//            log.info(depth.toString());
        }
    }

}
