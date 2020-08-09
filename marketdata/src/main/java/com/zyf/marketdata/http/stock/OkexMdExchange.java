package com.zyf.marketdata.http.stock;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.IMdExchange;
import com.zyf.baseservice.util.okex.OkexUtil;
import com.zyf.common.model.Depth;
import com.zyf.common.model.Kline;
import com.zyf.common.model.Precision;
import com.zyf.common.model.Ticker;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;

import java.util.List;
import java.util.Map;


/**
 * okexV3交易所实现类
 * @author yuanfeng.z
 * @date 2019/6/27 10:02
 */
public class OkexMdExchange implements IMdExchange {

    /**
     * 协议
     */
    private static final String PROTOCOL = "https://";

    /**
     * 网站
     */
    private static final String SITE = "www.okex.com";

    /**
     * url
     */
    private static final String URL = PROTOCOL +SITE;

    /**
     * get请求方法
     */
    private static final String METHOD_GET = "GET";

    /**
     * 交易所名字
     */
    private final ExchangeEnum name = ExchangeEnum.OKEXV3;

    /**
     * 价格、数量精度资源路径
     */
    private static final String PRECISION = "/api/spot/v3/instruments";

    /**
     * ticker资源路径
     */
    private static final String TICKER = "/api/spot/v3/instruments/{symbol}/ticker";

    /**
     * depth资源路径
     */
    private static final String DEPTH = "/api/spot/v3/instruments/{symbol}/book";

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
    public List<Kline> getKline(String symbol) {
        return null;
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

    /**
     * 获取币对精度
     * @return 币对精度列表
     */
    @Override
    public Map<String, Precision> getPrecisions() {
        /*获取币种精度数据*/
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(PRECISION);
        String str = OkHttpV3ClientProxy.get(sb.toString());

        /*适配*/
        JSONArray jo = JSONObject.parseArray(str);
        Map<String, Precision> map = OkexUtil.parsePrecisions(jo);
        return map;
    }
}
