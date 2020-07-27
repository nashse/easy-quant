package com.zyf.common.util;

import com.zyf.common.model.enums.ExchangeEnum;

/**
 * 架构工具
 * @author yuanfeng.z
 * @date 2019/7/4 19:19
 */
public class FrameworkUtil {

    /**
     * 字符串转ExchangeEnum枚举
     * @param exchangeName
     * @return
     */
    public static ExchangeEnum toExchangeEnum(String exchangeName) {
        if (exchangeName == null) { return null; }
        return ExchangeEnum.valueOf(exchangeName.toUpperCase());
    }
}
