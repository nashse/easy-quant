package com.zyf.baseservice.baseexchange.option;

import com.zyf.common.model.Depth;
import com.zyf.common.model.Precision;
import com.zyf.common.model.Ticker;
import com.zyf.common.model.future.Instrument;

import java.util.List;
import java.util.Map;

/**
 * 期权接口类
 * @author yuanfeng.z
 * @date 2019/7/19 10:22
 */
public interface IOptionExchange {
    /**
     * 获取ticker
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

    /**
     * 根据币种获取合约
     * @return
     */
    List<Instrument> getCurrencyInstrument(String currency);

    /**
     * 根据币种获取合约的数量精度
     * @return
     */
    Map<String, Precision> getCurrencyPrecisionsUSDT(String currency);

    /**
     * 根据合约获取合约的价格精度
     * @return
     */
    Precision getPrecision(String currency);
}
