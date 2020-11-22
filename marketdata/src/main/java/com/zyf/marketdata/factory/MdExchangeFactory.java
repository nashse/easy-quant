package com.zyf.marketdata.factory;

import com.zyf.baseservice.IMdExchange;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.model.enums.SecuritiesTypeEnum;
import com.zyf.common.util.CommomUtil;
import com.zyf.marketdata.http.future.HuobiProMdFutExchange;
import com.zyf.marketdata.http.future.OkexMdFutExchange;
import com.zyf.marketdata.http.option.DeribitMdExchange;
import com.zyf.marketdata.http.stock.HuobiProMdExchange;
import com.zyf.marketdata.http.stock.OkexMdExchange;

/**
 * 行情数据交易所工厂类
 *
 * @author yuanfeng.z
 * @date 2019/6/25 15:14
 */
public class MdExchangeFactory {

    /**
     * 生产工厂方法
     * @param exchangeName 交易所名字
     * @return
     */
    public static IMdExchange createMdExchange(ExchangeEnum exchangeName, SecuritiesTypeEnum type) {
        if (SecuritiesTypeEnum.STOCK.equals(type)) {
            if (exchangeName.equals(ExchangeEnum.OKEXV3)) {
                return OkexMdExchange.getInstance();
            } else if (exchangeName.equals(ExchangeEnum.HUOBIPRO)) {
                return HuobiProMdExchange.getInstance();
            } else if (exchangeName.equals(ExchangeEnum.DERIBIT)) {
                return DeribitMdExchange.getInstance();
            }
        }

        if (SecuritiesTypeEnum.FUTURE.equals(type)) {
           if (exchangeName.equals(ExchangeEnum.HUOBIPRO)) {
                return HuobiProMdFutExchange.getInstance();
            } else if (exchangeName.equals(ExchangeEnum.OKEXV3)) {
                return OkexMdFutExchange.getInstance();
            }
        }
        return null;
    }

    /**
     * 创建行情交易所实例
     *
     * @param exchangeName 交易所名字
     * @param type 证券类型
     * @return
     */
    public static IMdExchange createMdExchange(String exchangeName, String type) {
        ExchangeEnum exchangeType = CommomUtil.toExchangeEnum(exchangeName);
        SecuritiesTypeEnum securitiesType = CommomUtil.toSecuritiesTypeEnum(type);
        return MdExchangeFactory.createMdExchange(exchangeType, securitiesType);
    }
}
