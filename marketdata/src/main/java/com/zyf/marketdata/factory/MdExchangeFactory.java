package com.zyf.marketdata.factory;

import com.zyf.baseservice.IExchange;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.util.FrameworkUtil;
import com.zyf.marketdata.http.huobipro.HuobiProMdExchange;
import com.zyf.marketdata.http.okex.OkexMdExchange;
import com.zyf.marketdata.http.option.deribit.DeribitMdExchange;

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
    public static IExchange createMdExchange(ExchangeEnum exchangeName) {
        if (exchangeName.equals(ExchangeEnum.OKEXV3)) {
            return OkexMdExchange.getInstance();
        } else if (exchangeName.equals(ExchangeEnum.HUOBIPRO)) {
            return HuobiProMdExchange.getInstance();
        } else if (exchangeName.equals(ExchangeEnum.DERIBIT)) {
            return DeribitMdExchange.getInstance();
        }
        return null;
    }

    /**
     * 创建行情交易所实例
     *
     * @param exchangeName 交易所名字
     * @return
     */
    public static IExchange createMdExchange(String exchangeName) {
        ExchangeEnum exchangeType = FrameworkUtil.toExchangeEnum(exchangeName);
        return MdExchangeFactory.createMdExchange(exchangeType);
    }
}
