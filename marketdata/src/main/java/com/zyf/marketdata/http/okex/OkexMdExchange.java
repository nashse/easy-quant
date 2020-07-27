package com.zyf.marketdata.http.okex;

import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.baseexchange.BaseOkexExchange;
import com.zyf.baseservice.util.okex.OkexUtil;
import com.zyf.common.model.Depth;
import com.zyf.common.model.Ticker;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;
import com.zyf.marketdata.factory.MdExchangeFactory;


/**
 * okexV3交易所实现类
 * @author yuanfeng.z
 * @date 2019/6/27 10:02
 */
public class OkexMdExchange extends BaseOkexExchange {

    /**
     * ticker资源路径
     */
    private static final String TICKER = "/api/spot/v3/instruments/{symbol}/ticker";

    /**
     * depth资源路径
     */
    private static final String DEPTH = "/api/spot/v3/instruments/{symbol}/book";

    /**
     * 交易所名字
     */
    private final ExchangeEnum name = ExchangeEnum.OKEXV3;

    /**
     * 单例模式
     * @return
     */
    public static OkexMdExchange okexMdExchange = new OkexMdExchange();
    public static OkexMdExchange getInstance() {
        return okexMdExchange;
    }

    @Override
    public Ticker getTicker(String symbol) {
        // 转换币对格式
        symbol = OkexUtil.transfSymbol(symbol);

        /*获取ticker数据*/
        StringBuilder sb = new StringBuilder();
        String ticker = TICKER.replace("{symbol}", symbol);
        sb.append(URL).append(ticker);
        String tickerStr = OkHttpV3ClientProxy.get(sb.toString());

        /*适配*/
        JSONObject jo = JSONObject.parseObject(tickerStr);
        return OkexUtil.parseTicker(jo);
    }

    @Override
    public Depth getDepth(String symbol) {
        // 转换币对格式
        symbol = OkexUtil.transfSymbol(symbol);

        /*获取depth数据*/
        StringBuilder sb = new StringBuilder();
        final int size = 20;
        String depth = DEPTH.replace("{symbol}", symbol);
        sb.append(URL).append(depth).append("?").append("size=").append(size);
        String depthStr = OkHttpV3ClientProxy.get(sb.toString());

        /*适配*/
        JSONObject jo = JSONObject.parseObject(depthStr);
        return OkexUtil.parseDepth(jo);
    }

    public static void main(String[] args) {
        MdExchangeFactory.createMdExchange(ExchangeEnum.OKEXV3).getTicker("BTC/USDT");
        MdExchangeFactory.createMdExchange(ExchangeEnum.OKEXV3).getDepth("BTC/USDT");
        MdExchangeFactory.createMdExchange(ExchangeEnum.OKEXV3).getPrecisions();
    }
}
