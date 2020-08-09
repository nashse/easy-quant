package com.zyf.baseservice.util.okex;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * okex签名加密工具类
 * @author yuanfeng.z
 * @date 2019/6/28 9:30
 */
public class OkexSignUtil {

    /**
     * 字符编码
     */
    public final static String INPUT_CHARSET = "UTF-8";
    /**
     * 加密方法
     */
    private static final String SIGNATURE_METHOD = "HmacSHA256";
    /**
     * 超时时间
     */
    private final static int TIME_OUT = 30 * 60 * 1000;


    /**
     * url编码
     *
     * @param content
     * @param charset
     * @return
     */
    public static  String urlEncoded(String content, String charset) {
        try {
            return URLEncoder.encode(content, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("指定的编码集不对，您目前指定的编码集是：" + charset);
        }
    }

    public static  String urlEncoded(String content) {
        try {
            return URLEncoder.encode(content, OkexSignUtil.INPUT_CHARSET);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("指定的编码集不对，您目前指定的编码集是：" + OkexSignUtil.INPUT_CHARSET);
        }
    }

    /**
     * url解码
     *
     * @param content
     * @param charset
     * @return
     */
    public static String urlDecoded(String content, String charset) {
        try {
            return URLDecoder.decode(content, charset);
        } catch (UnsupportedEncodingException e){
            throw new RuntimeException("指定的编码集不对，您目前指定的编码集是：" + charset);
        }
    }

    public static String urlDecoded(String content) {
        try {
            return URLDecoder.decode(content, OkexSignUtil.INPUT_CHARSET);
        } catch (UnsupportedEncodingException e){
            throw new RuntimeException("指定的编码集不对，您目前指定的编码集是：" + OkexSignUtil.INPUT_CHARSET);
        }
    }

    /**
     * 签名
     *
     * @param method        请求类型
     * @param address       ip地址
     * @param resourcePath  资源路径
     * @param body          请求参数
     * @param apiKey        密钥
     * @param secretKey     私钥
     * @param passPhrase    密码
     * @return
     */
    public static LinkedHashMap<String, String> sign(String method, String address, LinkedHashMap<String, Object> body, String resourcePath, String apiKey, String secretKey, String passPhrase) {

        String epoch = formatEpoch(System.currentTimeMillis());

        StringBuilder s = new StringBuilder();
        s.append(epoch).append(method).append(resourcePath).append(body != null ? OkexUtil.addParams(body) : "");
        String sign = hmacSHA256(s.toString(), secretKey);

        LinkedHashMap lh = new LinkedHashMap<String, String>();
        lh.put("OK-ACCESS-KEY", apiKey);
        lh.put("OK-ACCESS-SIGN", sign);
        lh.put("OK-ACCESS-TIMESTAMP", epoch);
        lh.put("OK-ACCESS-PASSPHRASE", passPhrase);
        lh.put("x-simulated-trading",  "1");

        return lh;
    }

    public static String formatEpoch(Long time) {
        if (null == time) {
            return "";
        }
        return String.format("%s.%03d", time / 1000, time % 1000);
    }


    /**
     * HmacSHA256 加密
     *
     * @param msg   加密内容
     * @param secretKey   私钥
     */
    public static String hmacSHA256(String msg, String secretKey) {
        try {
            byte [] sign = OkexSignUtil.hmac(msg.getBytes(), secretKey, SIGNATURE_METHOD);
            return Base64.getEncoder().encodeToString(sign);
        } catch (Exception e) {
            System.out.println("Error:"+e.getStackTrace());
        }
        return null;
    }

    public static byte[] hmac(byte[] message, String key, String algorithm)
        throws NoSuchAlgorithmException, InvalidKeyException {
        Objects.requireNonNull(message, "message");
        SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), algorithm);
        Mac mac = Mac.getInstance(algorithm);
        mac.init(signingKey);
        return mac.doFinal(message);
    }

    /**
     * 获取UTC时间
     * @param delay 延迟
     * @return UTC时间
     */
    private static SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd'T'HH:mm:ss");
    private static Date getUTCTimeString() {
        Calendar cal = Calendar.getInstance();
        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);
        int dstOffset = cal.get(Calendar.DST_OFFSET);
        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));
        return new Date(cal.getTime().getTime());
    }


    public static void main(String[] args) {

        String appKey = "549cd388-d744-4e71-8290-18cb2c9a3748";
        String secret = "5B66D87B90E7C37CD4123105A6DA527B";
        String address = "https://www.okex.com";
        String passPrase = "123456mzy";
        String body = null;
        String resouecePath = "/api/spot/v3/accounts/btc";
        String method = "GET";

        LinkedHashMap<String, String> lh = OkexSignUtil.sign(method, address, null, resouecePath, appKey, secret, passPrase);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url(address + resouecePath)
                .get()
                .addHeader("OK-ACCESS-KEY", lh.get("OK-ACCESS-KEY"))
                .addHeader("OK-ACCESS-SIGN", lh.get("OK-ACCESS-SIGN"))
                .addHeader("OK-ACCESS-TIMESTAMP", lh.get("OK-ACCESS-TIMESTAMP"))
                .addHeader("OK-ACCESS-PASSPHRASE", lh.get("OK-ACCESS-PASSPHRASE"))
                .build();

        try (Response response = client.newCall(request).execute()) {
             System.out.println(response.body().string());
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
