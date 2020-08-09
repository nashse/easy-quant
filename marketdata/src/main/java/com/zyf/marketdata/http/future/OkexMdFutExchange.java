package com.zyf.marketdata.http.future;

import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.IMdExchange;
import com.zyf.baseservice.util.okex.OkexUtil;
import com.zyf.common.model.Depth;
import com.zyf.common.model.Kline;
import com.zyf.common.model.Precision;
import com.zyf.common.model.Ticker;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;
import com.zyf.marketdata.http.stock.OkexMdExchange;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * okex期货行情类
 *
 * @author yuanfeng.z
 * @date q 20:54
 */
@Slf4j
public class OkexMdFutExchange implements IMdExchange {


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
     * depth资源路径
     */
    private static final String DEPTH = "/api/swap/v3/instruments/<instrument_id>/depth";

    /**
     * 单例模式
     * @return
     */
    public static OkexMdFutExchange okexMdFutExchange = new OkexMdFutExchange();
    public static OkexMdFutExchange getInstance() {
        return okexMdFutExchange;
    }

    @Override
    public Ticker getTicker(String symbol) {
        return null;
    }

    @Override
    public List<Kline> getKline(String symbol) {
        return null;
    }

    @Override
    public Depth getDepth(String symbol) {
        /*获取depth数据*/
        StringBuilder sb = new StringBuilder();
        final int size = 10;
        String depth = DEPTH.replace("<instrument_id>", symbol);
        sb.append(URL).append(depth).append("?").append("size=").append(size);
        String depthStr = OkHttpV3ClientProxy.get(sb.toString());

        /*适配*/
        JSONObject jo = JSONObject.parseObject(depthStr);
        return OkexUtil.parseDepth(jo);
    }

    @Override
    public Map<String, Precision> getPrecisions() {
        return null;
    }
}
