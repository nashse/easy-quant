package com.zyf.common.exceptions;

import org.apache.commons.lang3.StringUtils;

/**
 * Exchange Http Api Error Code Enum
 * @author yuanfeng.z
 * @date 2019/6/25 10:27
 */
public enum EHAErrorCodeEnum implements ErrorCode {

    /** 未指明的异常 */
    UNSPECIFIED("500", "网络异常，延迟"),
    NO_SERVICE("404", "网络异常, 服务器熔断"),

    /** 接口异常 */
    API_GET_TICKER("12001","获取ticker异常"),
    API_GET_KLINE("12002","获取kline异常"),
    API_GET_DEPTH("12003","获取深度异常"),
    API_GET_PRECISIONS("12004","获取精度异常"),
    API_GET_ACCOUNT("12005","获取账户信息异常"),
    API_GET_ASSET("12006","获取合约账户信息异常"),
    API_BUY_MARKET("12007","市价买入异常"),
    API_BUY_LIMIT("12008","限价买入异常"),
    API_SELL_MARKET("12009","市价卖出异常"),
    API_SELL_LIMIT("12010","限价卖出异常"),
    API_CANCEL_ORDER("12011","根据订单id撤单异常"),
    API_CANCEL_ORDERS("12012","批量撤单异常"),
    API_GET_ORDERS("12013","获取所有的订单异常"),
    API_GET_PENDING_ORDERS("12014","获取所有的正在挂的订单异常"),
    API_GET_ORDER("12015","获取指定id的订单信息异常"),
    API_GET_TRADE("12016","获取成交明细异常"),
    API_GET_INSTRUMENT_TRADE("12017","获取合约成交明细异常"),
    API_GET_CONTRACT("12018","根据合约获取当前仓位持仓张数异常"),
    API_GET__UNSETTLED_ORDERS("12019","根据合约获取未成交订单异常"),
    API_CANCEL_ALL("12020","API 根据合约撤掉所有订单异常"),
    API_PLACE_ORDERS("12021","合约批量下单异常"),

    // 通用异常
    ACCOUNT_BALANCE("11001", "账户余额不足"),
    QUANTITY_LOW("11002","下单量过低");

    /** 错误码 */
    private final String code;

    /** 描述 */
    private final String description;

    /**
     * @param code 错误码
     * @param description 描述
     */
    private EHAErrorCodeEnum(final String code, final String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据编码查询枚举。
     *
     * @param code 编码。
     * @return 枚举。
     */
    public static EHAErrorCodeEnum getByCode(String code) {
        for (EHAErrorCodeEnum value : EHAErrorCodeEnum.values()) {
            if (StringUtils.equals(code, value.getCode())) {
                return value;
            }
        }
        return UNSPECIFIED;
    }

    /**
     * 枚举是否包含此code
     * @param code 枚举code
     * @return 结果
     */
    public static Boolean contains(String code){
        for (EHAErrorCodeEnum value : EHAErrorCodeEnum.values()) {
            if (StringUtils.equals(code, value.getCode())) {
                return true;
            }
        }
        return  false;
    }

    @Override
    public String getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }
}