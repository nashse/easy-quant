package com.zyf.marketdata.http.future;

import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.IMdExchange;
import com.zyf.baseservice.util.huobipro.HuobiProFutUtil;
import com.zyf.common.model.Depth;
import com.zyf.common.model.Kline;
import com.zyf.common.model.Precision;
import com.zyf.common.model.Ticker;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.model.future.InstrumentFut;
import com.zyf.common.model.option.Instrument;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 火币期货行情类
 *
 * @author yuanfeng.z
 * @date q 20:54
 */
@Slf4j
public class HuobiProMdFutExchange implements IMdExchange {

    /**
     * 协议
     */
    private static final String PROTOCOL = "https://";

    /**
     * 网站
     */
    private static final String SITE = "api.hbdm.com";

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
     * 永续合约depth资源路径
     */
    private static final String DEPTH = "/swap-ex/market/depth";

    /**
     * 交割合约depth资源路径
     */
    private static final String DELIVERY_CONTRACT_DEPTH = "/market/depth";

    /**
     * 获取交割合约资源路径
     */
    private static final String ALL_DELIVERY_CONTRACT = "/api/v1/contract_contract_info";

    /**
     * 获取kline资源路径
     */
    private static final String KLINES = "/swap-ex/market/history/kline";

    /**
     * 单例模式
     *
     * @return
     */
    public static HuobiProMdFutExchange huobiProMdFutExchange = new HuobiProMdFutExchange();

    public static HuobiProMdFutExchange getInstance() {
        return huobiProMdFutExchange;
    }

    private HuobiProMdFutExchange() {
        super();
    }

    @Override
    public Ticker getTicker(String symbol) {
        return null;
    }

    @Override
    public List<Kline> getKline(String symbol, String granularity) {
        symbol = HuobiProFutUtil.transfSymbol(symbol);

        /*获取depth数据*/
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(KLINES)
                .append("?").append("contract_code=").append(symbol)
                .append("&").append("period=").append(granularity)
                .append("&").append("size=").append(150);
        String klineStr;
        try {
            klineStr = OkHttpV3ClientProxy.get(sb.toString());
        } catch (Exception e) {
            log.error("获取huobi kline失败,错误异常:" + e.getMessage());
            return null;
        }
        /*适配*/
        JSONObject jo = JSONObject.parseObject(klineStr);
        return HuobiProFutUtil.parseKline(jo);

    }

    @Override
    public Depth getDepth(String symbol) {
        // 转换币对格式
        symbol = HuobiProFutUtil.transfSymbol(symbol);

        /*获取depth数据*/
        final String type = "step0";
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(DEPTH)
                .append("?").append("contract_code=").append(symbol)
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
        return HuobiProFutUtil.parseDepth(jo);
    }

    /**
     * 获取所有交割合约名
     *
     * @return
     */
    public static List<InstrumentFut> getDeliveryContracts() {
        /*获取symbol数据*/
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(ALL_DELIVERY_CONTRACT);
        String depthStr;
        try {
            depthStr = OkHttpV3ClientProxy.get(sb.toString());
        } catch (Exception e) {
            log.error("获取huobi交割合约名失败,错误异常:" + e.getMessage());
            return null;
        }
        /*适配*/
        JSONObject jo = JSONObject.parseObject(depthStr);
        return HuobiProFutUtil.parseDeliveryContracts(jo);
    }

    /**
     * 获取交割合约深度
     *
     * @param symbol
     * @return
     */
    public static Depth getDeliveryDepth(String symbol) {
        // 转换币对格式
        symbol = HuobiProFutUtil.transfSymbol(symbol);

        /*获取depth数据*/
        final String type = "step6";
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(DELIVERY_CONTRACT_DEPTH)
                .append("?").append("symbol=").append(symbol)
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
        return HuobiProFutUtil.parseDepth(jo);
    }


    @Override
    public Map<String, Precision> getPrecisions() {
        return null;
    }


    public static void main(String[] args) {
        HuobiProMdFutExchange.getInstance().getKline("BTC-USD", "1min");
        Map<String, String> map = new HashMap<>();
        map.put("this_week", "CW");
        map.put("next_week", "NW");
        map.put("quarter", "CQ");
        map.put("next_quarter", "NQ");
        List<InstrumentFut> list = HuobiProMdFutExchange.getDeliveryContracts();
        for (InstrumentFut i : list) {
            StringBuilder sb = new StringBuilder();
            sb.append(i.getBaseCurrency()).append("_").append(map.get(i.getContractType()));
            Depth d = HuobiProMdFutExchange.getDeliveryDepth(sb.toString());
            log.info(d.toString());
        }
    }
}
