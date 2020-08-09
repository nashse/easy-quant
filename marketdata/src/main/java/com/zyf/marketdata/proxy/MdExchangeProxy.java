package com.zyf.marketdata.proxy;

import com.zyf.baseservice.IMdExchange;
import com.zyf.baseservice.model.enums.CacheNameEnum;
import com.zyf.common.model.Depth;
import com.zyf.common.model.Precision;
import com.zyf.common.model.Ticker;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.model.enums.SecuritiesTypeEnum;
import com.zyf.common.util.EhcacheUtil;
import com.zyf.common.util.CommomUtil;
import com.zyf.marketdata.factory.MdExchangeFactory;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.util.Map;

/**
 * 行情代理类（1.异常统一处理。2.缓存。3.多次获取）
 *
 * @author yuanfeng.z
 * @date 2019/7/4 11:53
 */
@Getter
@ToString
@EqualsAndHashCode
public class MdExchangeProxy {
    /**
     * 交易交易所对象
     */
    private IMdExchange mdExchange;

    /**
     * 交易所名称（枚举）
     */
    private ExchangeEnum exchangeNameEnum;

    /**
     * 交易所名字
     */
    private String exchangeName;

    public MdExchangeProxy(ExchangeEnum name, SecuritiesTypeEnum type) {
        this.mdExchange = MdExchangeFactory.createMdExchange(name, type);
        this.exchangeNameEnum = name;
        this.exchangeName = name.getValue();
        EhcacheUtil.initCacheManager();
        EhcacheUtil.createCache(CacheNameEnum.TICKER.getValue(), String.class, Ticker.class);
        EhcacheUtil.createCache(CacheNameEnum.DEPTH.getValue(), String.class, Depth.class);
    }

    public MdExchangeProxy(String exchangeName, String type) {
        this(CommomUtil.toExchangeEnum(exchangeName), CommomUtil.toSecuritiesTypeEnum(type));
    }

    /**
     * 获取ticker
     *
     * @param symbol 币对
     * @return
     */
    public Ticker getTicker(String symbol) {
        Ticker ticker = (Ticker) EhcacheUtil.get(CacheNameEnum.TICKER.getValue(), symbol);
        if (ticker == null) {
            ticker = this.mdExchange.getTicker(symbol);
            if (ticker != null) {
                EhcacheUtil.put(CacheNameEnum.TICKER.getValue(), symbol, ticker);
            }
        }
        return ticker;
    }

    /**
     * 获取深度
     *
     * @param symbol 币对
     * @return
     */
    public Depth getDepth(String symbol) {
        Depth depth = (Depth) EhcacheUtil.get(CacheNameEnum.DEPTH.getValue(), symbol);
        if (depth == null) {
            depth = this.mdExchange.getDepth(symbol);
            if (depth != null) {
                EhcacheUtil.put(CacheNameEnum.DEPTH.getValue(), symbol, depth);
            }
        }
        return depth;
    }

    /**
     * 获取精度
     *
     * @return
     */
    public Map<String, Precision> getPrecisions() {
        Map<String, Precision> precisionMap = this.mdExchange.getPrecisions();
        return precisionMap;
    }
}
