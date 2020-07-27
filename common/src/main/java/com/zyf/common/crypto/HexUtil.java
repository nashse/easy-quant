package com.zyf.common.crypto;

import java.util.Objects;

/**
 * 16进制编码工具
 *
 * @author yuanfeng.z
 * @version 1.0
 * @date 2019-06-27
 */
public class HexUtil {

    /**
     * 16进制大写编码
     */
    private static final char[] HEX_CODE_UPPER = "0123456789ABCDEF".toCharArray();
    /**
     * 16进制小写编码
     */
    private static final char[] HEX_CODE_LOWER = "0123456789abcdef".toCharArray();

    /**
     * 16进制编码
     *
     * @param plain 编码数据
     * @param upper 是否大写
     * @return 编码后字符串
     */
    public static String hexEncode(byte[] plain, boolean upper) {
        Objects.requireNonNull(plain, "plain");
        StringBuilder sb = new StringBuilder(plain.length << 1);
        if (upper) {
            for (byte b : plain) {
                sb.append(HEX_CODE_UPPER[(b >> 4) & 0XF])
                        .append(HEX_CODE_UPPER[b & 0XF]);
            }
        } else {
            for (byte b : plain) {
                sb.append(HEX_CODE_LOWER[(b >> 4) & 0XF])
                        .append(HEX_CODE_LOWER[b & 0XF]);
            }
        }
        return sb.toString();
    }

    /**
     * 16进制编码
     *
     * @param plain 编码数据
     * @return 大写16进制编码
     */
    public static String hexEncodeUpper(byte[] plain) {
        return hexEncode(plain, true);
    }

    /**
     * 16进制编码
     *
     * @param plain 编码前数据
     * @return 小写16进制编码
     */
    public static String hexEncodeLower(byte[] plain) {
        return hexEncode(plain, false);
    }

    /**
     * 16进制解码
     *
     * @param hex 16进制编码数据
     * @return 原始数据
     */
    public static byte[] hexDecode(String hex) {
        Objects.requireNonNull(hex, "hex");
        int l = hex.length();
        if (1 == hex.length() % 2) {
            throw new RuntimeException("the length of hex must be even: " + hex.length());
        }
        byte[] bytes = new byte[l / 2];
        for (int i = 0; i < l; i += 2) {
            bytes[i / 2] = (byte) ((hex2int(hex.charAt(i)) << 4) + hex2int(hex.charAt(i + 1)));
        }
        return bytes;
    }

    /**
     * 字符串转换为16进制字符串
     *
     * @param s
     * @return
     */
    public static String stringToHexString(String s) {
        String str = "";
        for (int i = 0; i < s.length(); i++) {
            int ch = s.charAt(i);
            String s4 = Integer.toHexString(ch);
            str = str + s4;
        }
        return str;
    }

    /**
     * 16进制字符串转换为字符串
     *
     * @param s
     * @return
     */
    public static String hexStringToString(String s) {
        if (s == null || s.equals("")) {
            return null;
        }
        s = s.replace(" ", "");
        byte[] baKeyword = new byte[s.length() / 2];
        for (int i = 0; i < baKeyword.length; i++) {
            try {
                baKeyword[i] = (byte) (0xff & Integer.parseInt(
                        s.substring(i * 2, i * 2 + 2), 16));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        try {
            s = new String(baKeyword, "utf-8");
            new String();
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        return s;
    }


    /**
     * 16进制字符转16进制数
     *
     * @param c 字符
     * @return 16进制数
     */
    private static int hex2int(char c) {
        if ('0' <= c && c <= '9') {
            return c - '0';
        }
        if ('a' <= c && c <= 'f') {
            return c - 'a' + 10;
        }
        if ('A' <= c && c <= 'F') {
            return c - 'A' + 10;
        }
        return 0;
    }

}
