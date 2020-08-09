package com.zyf.marketdata.http.stock;

import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.IMdExchange;
import com.zyf.baseservice.util.huobipro.HuobiProUtil;
import com.zyf.common.model.Depth;
import com.zyf.common.model.Kline;
import com.zyf.common.model.Precision;
import com.zyf.common.model.Ticker;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 火币pro交易所行情实现类
 *
 * @author yuanfeng.z
 * @date 2019/6/25 10:02
 */
@Slf4j
public class HuobiProMdExchange implements IMdExchange {

    /**
     * 协议
     */
    private static final String PROTOCOL = "https://";

    /**
     * 网站
     */
    private static final String SITE = "api.huobi.pro";

    /**
     * url
     */
    private static final String URL = PROTOCOL + SITE;

    /**
     * 价格、数量精度资源路径
     */
    private static final String PRESISION = "/v1/common/symbols";

    /**
     * 交易所名字
     */
    private final ExchangeEnum name = ExchangeEnum.HUOBIPRO;

    /**
     * ticker资源路径
     */
    private static final String TICKER = "/market/detail/merged";

    /**
     * depth资源路径
     */
    private static final String DEPTH = "/market/depth";

    /**
     * 单例模式
     *
     * @return
     */
    public static HuobiProMdExchange huobiProMdExchange = new HuobiProMdExchange();

    public static HuobiProMdExchange getInstance() {
        return huobiProMdExchange;
    }

    private HuobiProMdExchange() {
        super();
    }

    /**
     * 获取指定币对市场心跳数据
     *
     * @param symbol 币对
     * @return
     */
    @Override
    public Ticker getTicker(String symbol) {
        // 转换币对格式
        symbol = HuobiProUtil.transfSymbol(symbol);

        /*获取ticker数据*/
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(TICKER).append("?").append("symbol=").append(symbol);
        String tickerStr = OkHttpV3ClientProxy.get(sb.toString());

        /*适配*/
        JSONObject jo = JSONObject.parseObject(tickerStr);
        Ticker ticker = HuobiProUtil.parseTicker(jo);
        return ticker;
    }

    @Override
    public List<Kline> getKline(String symbol) {
        return null;
    }

    /**
     * 获取指定币对市场深度数据
     *
     * @param symbol 币对
     * @return
     */
    @Override
    public Depth getDepth(String symbol) {
        // 转换币对格式
        symbol = HuobiProUtil.transfSymbol(symbol);

        /*获取depth数据*/
        final int depth = 20;
        final String type = "step0";
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(DEPTH)
                .append("?").append("symbol=").append(symbol)
                .append("&").append("depth=").append(depth)
                .append("&").append("type=").append(type);
        String depthStr;
        try {
            depthStr = OkHttpV3ClientProxy.get(sb.toString());
        } catch (Exception e) {
            log.error("获取huobi盘口失败,错误异常:" + e.getMessage());
            return null;
        }
        /*适配*/
        JSONObject jo = JSONObject.parseObject(depthStr);
        return HuobiProUtil.parseDepth(jo);
    }

    /**
     * 获取币对精度
     *
     * @return 币对精度列表
     */
    @Override
    public Map<String, Precision> getPrecisions() {
        /*获取币种精度数据*/
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(PRESISION);
        String accuracysStr = OkHttpV3ClientProxy.get(sb.toString());

        /*适配*/
        JSONObject jo = JSONObject.parseObject(accuracysStr);
        Map<String, Precision> precisions = HuobiProUtil.parsePrecisions(jo);
        return precisions;
    }

    public static void main(String[] args) {
        log.info(HuobiProMdExchange.getInstance().getDepth("bchusdt").toString());
    }

}
