package com.zyf.baseservice.baseexchange.future;

import com.zyf.common.model.Depth;
import com.zyf.common.model.Precision;
import com.zyf.common.model.Ticker;
import com.zyf.common.model.future.Index;
import com.zyf.common.model.future.Instrument;

import java.util.List;
import java.util.Map;

/**
 * 期货接口类
 * @author yuanfeng.z
 * @date 2019/7/19 10:22
 */
public interface IFutureExchange {

    /**
     * 获取指数
     * @param indexSymbol 指数合约
     * @param count 数量
     * @return
     */
    List<Index> getIndex(String indexSymbol, Integer count);

    /**
     * 获取ticker
     *
     * @param symbol 币对
     * @return
     */
    Ticker getTicker(String symbol);


    /**
     * 获取深度
     * @param symbol 币对
     * @return
     */
    Depth getDepth(String symbol);

    /**
     * 获取精度
     * @return
     */
    Map<String, Precision> getPrecisions();


    /**
     * 获取所有合约信息
     * @return
     */
    List<Instrument> getInstruments();


}
