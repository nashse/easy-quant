package com.zyf.common.util;

import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.model.enums.SecuritiesTypeEnum;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 通用工具类
 * @author yuanfeng.z
 * @date 2019/7/4 19:19
 */
public class CommomUtil {

    /**
     * 字符串转ExchangeEnum枚举
     * @param exchangeName
     * @return
     */
    public static ExchangeEnum toExchangeEnum(String exchangeName) {
        if (exchangeName == null) { return null; }
        return ExchangeEnum.valueOf(exchangeName.toUpperCase());
    }

    /**
     * 字符串转SecuritiesTypeEnum枚举
     * @param type 证券类型
     * @return
     */
    public static SecuritiesTypeEnum toSecuritiesTypeEnum(String type) {
        if (type == null) { return null; }
        return SecuritiesTypeEnum.valueOf(type.toUpperCase());
    }

    /**
     * 重复执行合约市价下单函数
     * @param instance
     * @param methodName
     * @param rtnFail
     * @param time
     * @param objects
     * @param <E>
     * @param <T>
     * @return
     */
    public static <E, T> T repeatOrderMarket(E instance,
                                  String methodName,
                                  T rtnFail,
                                  int time,
                                  Object... objects) {
        while (time-- != 0) {
            try {
                Method method = instance.getClass().getDeclaredMethod(methodName,
                        String.class,
                        BigDecimal.class,
                        Integer.class);
                T rtn = (T) method.invoke(instance, objects);
                if (rtn.equals(rtnFail)) { continue; }
                return rtn;
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return rtnFail;
    }

    /**
     * 睡眠
     * @param time
     */
    public static void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
