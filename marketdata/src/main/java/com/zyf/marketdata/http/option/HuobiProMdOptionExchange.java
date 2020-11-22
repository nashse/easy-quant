package com.zyf.marketdata.http.option;

import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.IMdExchange;
import com.zyf.baseservice.util.huobipro.HuobiProFutUtil;
import com.zyf.baseservice.util.huobipro.HuobiProOptionUtil;
import com.zyf.common.model.Depth;
import com.zyf.common.model.Kline;
import com.zyf.common.model.Precision;
import com.zyf.common.model.Ticker;
import com.zyf.common.model.option.Instrument;
import com.zyf.common.model.option.OptionDetailInfo;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Map;

/**
 * 火币期权行情类
 *
 * @author yuanfeng.z
 * @date q 20:54
 */
@Slf4j
public class HuobiProMdOptionExchange implements IMdExchange {

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
     * 期权合约
     */
    private static final String OPTION_CONTRACT = "/option-api/v1/option_contract_info";

    /**
     * depth资源路径
     */
    private static final String DEPTH = "/option-ex/market/depth";

    /**
     * 查询合约市场指标 （包含iv）
     */
    private static final String MARKET_INDEX = "/option-api/v1/option_market_index";


    @Override
    public Ticker getTicker(String symbol) {
        return null;
    }

    @Override
    public List<Kline> getKline(String symbol, String granularity) {
        return null;
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

    @Override
    public Map<String, Precision> getPrecisions() {
        return null;
    }

    /**
     * 查询合约市场指标 （包含iv）
     * @return
     */
    public static List<OptionDetailInfo> getOptionDetailInfo(String instrumentName) {
        /*获取symbol数据*/
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(MARKET_INDEX).
                append("?").append("contract_code=").
                append(instrumentName);
        String depthStr;
        try {
            depthStr = OkHttpV3ClientProxy.get(sb.toString());
        } catch (Exception e) {
            log.error("获取huobi合约市场指标名失败,错误异常:" + e.getMessage());
            return null;
        }
        /*适配*/
        JSONObject jo = JSONObject.parseObject(depthStr);
        return HuobiProOptionUtil.parseOptionDetailInfo(jo);
    }

    /**
     * 获取合约信息
     * @return
     */
    public static List<Instrument> getContractInfo() {
        /*获取symbol数据*/
        StringBuilder sb = new StringBuilder();
        sb.append(URL).append(OPTION_CONTRACT);
        String depthStr;
        try {
            depthStr = OkHttpV3ClientProxy.get(sb.toString());
        } catch (Exception e) {
            log.error("获取huobi期权合约名失败,错误异常:" + e.getMessage());
            return null;
        }
        /*适配*/
        JSONObject jo = JSONObject.parseObject(depthStr);
        return HuobiProOptionUtil.parseContractInfo(jo);
    }


    public static void main(String[] args) {
        List<Instrument> instruments = HuobiProMdOptionExchange.getContractInfo();
        HuobiProMdOptionExchange ex = new HuobiProMdOptionExchange();
        for (Instrument i : instruments) {
            Depth  df = ex.getDepth(i.getName());
            log.info(HuobiProMdOptionExchange.getOptionDetailInfo(i.getName()).toString());
//            log.info(ex.getDepth(i.getName()).toString());
        }

        log.info(instruments.toString());
    }
}
