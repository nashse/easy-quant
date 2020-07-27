package com.zyf.common.model.enums;

/**
 * 交易所枚举类
 * @author yuanfeng.z
 * @date 2019/6/25 15:25
 */
public enum ExchangeEnum {
    BHEX("bhex"),
    OKEXV3("okexv3"),
    HUOBIPRO("huobipro"),
    BINANCE("binance"),
    EMEX("emex"),
    HKSTOX("hkstox"),
    BITMEX("bitmex"),
    DERIBIT("deribit"),
    BIBOX("bibox"),
    COINEX("coinex"),
    FTX("ftx"),
    COINBENE("coinbene"),
    COINEX_PERPETUAL("coinex_perpetual"),
    GATEIO("gateio"),
    CRYPTO("crypto"),
    NULL("null");

    private String value;

    ExchangeEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static ExchangeEnum getEnum(String exchange){
        for (ExchangeEnum e : ExchangeEnum.values()) {
            if(e.getValue().equals(exchange)){
                return e;
            }
        }
        return ExchangeEnum.NULL;
    }
}
