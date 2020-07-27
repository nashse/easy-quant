package com.zyf.trade.factory;

import com.zyf.baseservice.IExchange;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.util.FrameworkUtil;
import com.zyf.trade.http.deribit.DeribitTradeExchange;
import com.zyf.trade.http.huobipro.HuobiProTradeExchange;
import com.zyf.trade.http.okex.OkexTradeExchange;

/**
 * 行情数据交易所工厂类
 * @author yuanfeng.z
 * @date 2019/6/25 15:14
 */
public class TradeExchangeFactory {

    public static IExchange createTradeExchange(ExchangeEnum type, String accessKey, String secretKey) {
        if (type.equals(ExchangeEnum.OKEXV3)) {
            return new OkexTradeExchange(accessKey, secretKey);
        } else if (type.equals(ExchangeEnum.HUOBIPRO)) {
            return new HuobiProTradeExchange(accessKey, secretKey);
        } else if (type.equals(ExchangeEnum.DERIBIT)) {
            return new DeribitTradeExchange(accessKey, secretKey);
        }
        return null;
     }

    /**
     * 创建交易交易所类
     * @param exchangeName 交易所名字
     * @param accessKey 公钥
     * @param secretKey 密钥
     * @return
     */
    public static IExchange createTradeExchange(String exchangeName, String accessKey, String secretKey) {
        ExchangeEnum exchangeType = FrameworkUtil.toExchangeEnum(exchangeName);
        return TradeExchangeFactory.createTradeExchange(exchangeType, accessKey, secretKey);
    }
}
