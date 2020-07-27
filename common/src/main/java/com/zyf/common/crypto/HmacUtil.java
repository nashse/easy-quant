package com.zyf.common.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

/**
 * Hmac加密工具
 *
 * @author yuanfeng.z
 * @version 1.0
 * @date 2019-06-27
 */
public class HmacUtil {

    /**
     * Hmac加密
     *
     * @param message
     * @param key
     * @param hmacType
     * @return
     * @throws Exception
     */
    public static byte[] hmac(byte[] message, String key, HmacType hmacType) throws Exception {
        Objects.requireNonNull(message, "message");
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), hmacType.getName());
        Mac mac = Mac.getInstance(hmacType.getName());
        mac.init(signingKey);
        return mac.doFinal(message);
    }

    /**
     * Hmac加密
     *
     * @param message
     * @param key
     * @param hmacType
     * @param upper    是否大写
     * @return
     */
    private static String hmac(byte[] message, String key, HmacType hmacType, boolean upper) {
        try {
            byte[] bytes = hmac(message, key, hmacType);
            return HexUtil.hexEncode(bytes, upper);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String HmacMD5(String message, String key) {
        return hmac(message.getBytes(), key, HmacType.HMAC_MD5, true);
    }

    public static String HmacSHA1(String message, String key) {
        return hmac(message.getBytes(), key, HmacType.HMAC_SHA1, true);
    }

    public static String HmacSHA256(String message, String key) {
        return hmac(message.getBytes(), key, HmacType.HMAC_SHA256, true);
    }

    public static String HmacSHA384(String message, String key) {
        return hmac(message.getBytes(), key, HmacType.HMAC_SHA384, true);
    }

    public static String HmacSHA512(String message, String key) {
        return hmac(message.getBytes(), key, HmacType.HMAC_SHA512, true);
    }

    public static String hmacMD5(String message, String key) {
        return hmac(message.getBytes(), key, HmacType.HMAC_MD5, false);
    }

    public static String hmacSHA1(String message, String key) {
        return hmac(message.getBytes(), key, HmacType.HMAC_SHA1, false);
    }

    public static String hmacSHA256(String message, String key) {
        return hmac(message.getBytes(), key, HmacType.HMAC_SHA256, false);
    }

    public static String hmacSHA384(String message, String key) {
        return hmac(message.getBytes(), key, HmacType.HMAC_SHA384, false);
    }

    public static String hmacSHA512(String message, String key) {
        return hmac(message.getBytes(), key, HmacType.HMAC_SHA512, false);
    }

    /**
     * 利用java原生的类实现SHA256加密
     * @param str 加密后的报文
     * @return
     */
    public static String getSHA256(String str) {
        MessageDigest messageDigest;
        String encodestr = "";
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            messageDigest.update(str.getBytes("UTF-8"));
            encodestr = byte2Hex(messageDigest.digest());
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return encodestr;
    }

    /**
     * 将byte转为16进制
     * @param bytes
     * @return
     */
    private static String byte2Hex(byte[] bytes) {
        StringBuffer stringBuffer = new StringBuffer();
        String temp = null;
        for (int i = 0; i < bytes.length; i++) {
            temp = Integer.toHexString(bytes[i] & 0xFF);
            if (temp.length() == 1) {
    //1得到一位的进行补0操作
                stringBuffer.append("0");
            }
            stringBuffer.append(temp);
        }
        return stringBuffer.toString();
    }

}
