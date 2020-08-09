package com.zyf.trade.factory;

import com.zyf.baseservice.ITradeExchange;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.model.enums.SecuritiesTypeEnum;
import com.zyf.common.util.CommomUtil;
import com.zyf.trade.http.future.HuobiProTradeFutExchange;
import com.zyf.trade.http.future.OkexTradeFutExchange;
import com.zyf.trade.http.stock.DeribitTradeExchange;
import com.zyf.trade.http.stock.HuobiProTradeExchange;
import com.zyf.trade.http.stock.OkexTradeExchange;

/**
 * 行情数据交易所工厂类
 * @author yuanfeng.z
 * @date 2019/6/25 15:14
 */
public class TradeExchangeFactory {

    /**
     * 创建实例
     * @param exchangeName 交易所名字
     * @param type 证券类型
     * @param accessKey 公钥
     * @param secretKey 密钥
     * @return
     */
    public static ITradeExchange createTradeExchange(ExchangeEnum exchangeName, SecuritiesTypeEnum type, String accessKey, String secretKey) {
        if (SecuritiesTypeEnum.STOCK.equals(type)) {
            if (exchangeName.equals(ExchangeEnum.OKEXV3)) {
                return new OkexTradeExchange(accessKey, secretKey);
            } else if (exchangeName.equals(ExchangeEnum.HUOBIPRO)) {
                return new HuobiProTradeExchange(accessKey, secretKey);
            } else if (exchangeName.equals(ExchangeEnum.DERIBIT)) {
                return new DeribitTradeExchange(accessKey, secretKey);
            }
        }

        if (SecuritiesTypeEnum.FUTURE.equals(type)) {
            if (exchangeName.equals(ExchangeEnum.HUOBIPRO)) {
                return new HuobiProTradeFutExchange(accessKey, secretKey);
            } else if (exchangeName.equals(ExchangeEnum.OKEXV3)) {
                return new OkexTradeFutExchange(accessKey, secretKey);
            }
        }

        return null;
     }

    /**
     * 创建交易交易所类
     * @param exchangeName 交易所名字
     * @param type 证券类型
     * @param accessKey 公钥
     * @param secretKey 密钥
     * @return
     */
    public static ITradeExchange createTradeExchange(String exchangeName, String type, String accessKey, String secretKey) {
        ExchangeEnum exchangeType = CommomUtil.toExchangeEnum(exchangeName);
        SecuritiesTypeEnum securitiesType = CommomUtil.toSecuritiesTypeEnum(type);
        return TradeExchangeFactory.createTradeExchange(exchangeType, securitiesType, accessKey, secretKey);
    }
}
