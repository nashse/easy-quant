package com.zyf.marketdata.http.huobipro;

import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.baseexchange.BaseHuobiProExchange;
import com.zyf.baseservice.util.huobipro.HuobiProUtil;
import com.zyf.common.model.Depth;
import com.zyf.common.model.Ticker;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;
import lombok.extern.log4j.Log4j2;
import lombok.extern.slf4j.Slf4j;

/**
 * 火币pro交易所行情实现类
 * @author yuanfeng.z
 * @date 2019/6/25 10:02
 */
@Slf4j
public class HuobiProMdExchange extends BaseHuobiProExchange {
    /**
     * ticker资源路径
     */
    private static final String TICKER = "/market/detail/merged";

    /**
     * depth资源路径
     */
    private static final String DEPTH = "/market/depth";

    /**
     * 交易所名字
     */
    private final ExchangeEnum name = ExchangeEnum.HUOBIPRO;

    /**
     * 单例模式
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

    /**
     * 获取指定币对市场深度数据
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
        }catch (Exception e){
            log.error("获取huobi盘口失败,错误异常:"+e.getMessage());
            return null;
        }
        /*适配*/
        JSONObject jo = JSONObject.parseObject(depthStr);
        return HuobiProUtil.parseDepth(jo);
    }

    public static void main(String[] args) {
        log.info(HuobiProMdExchange.getInstance().getDepth("bchusdt").toString());
    }

}
