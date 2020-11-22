package com.zyf.baseservice;

import com.zyf.common.model.*;
import com.zyf.common.model.enums.Side;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

/**
 * 行情交易所接口
 * <br> 公有接口
 * @author yuanfeng.z
 * @date 2019/6/28 15:33
 */
public interface IMdExchange {
    /**
     * 获取ticker
     *
     * @param symbol 币对
     * @return
     */
    Ticker getTicker(String symbol);

    /**
     * 获取kline
     *
     * @param symbol 币对
     * @param granularity 级别
     * @return
     */
    List<Kline> getKline(String symbol, String granularity);

    /**
     * 获取深度
     *
     * @param symbol 币对
     * @return
     */
    Depth getDepth(String symbol);

    /**
     * 获取精度
     *
     * @return
     */
    Map<String, Precision> getPrecisions();
}
