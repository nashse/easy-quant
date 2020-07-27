package com.zyf.common.crypto;

/**
 * Hmac加密类型
 *
 * @author yuanfeng.z
 * @version 1.0
 * @date 2019-06-27
 */
public enum HmacType {

    HMAC_MD5("HmacMD5"),
    HMAC_SHA1("HmacSHA1"),
    HMAC_SHA256("HmacSHA256"),
    HMAC_SHA384("HmacSHA384"),
    HMAC_SHA512("HmacSHA512");

    private String name;

    HmacType(String value) {
        this.name = value;
    }

    public String getName() {
        return name;
    }

}
