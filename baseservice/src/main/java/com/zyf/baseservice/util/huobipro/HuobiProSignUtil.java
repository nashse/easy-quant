package com.zyf.baseservice.util.huobipro;

import com.zyf.common.model.enums.HttpMethod;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;


/**
 * huobipro签名加密工具类
 * @author yuanfeng.z
 * @date 2019/6/27 18:10
 */
public class HuobiProSignUtil {
    /**
     * 加密方法
     */
    private static final String SIGNATURE_METHOD = "HmacSHA256";
    /**
     * 超时时间
     */
    private final static int TIME_OUT = 30 * 60 * 1000;

    /**
     * 拼接参数
     *
     * @param paramMap
     */
    public static String addParams(LinkedHashMap<String, String> paramMap) {
        String rtn = "";
        // 按key排序
        paramMap = HuobiProSignUtil.sort(paramMap);
        /*拼接字符串*/
        for (Map.Entry<String, String> pair : paramMap.entrySet()) {
            String key = HuobiProSignUtil.urlEncoded(pair.getKey(), StandardCharsets.UTF_8.toString());
            String value = HuobiProSignUtil.urlEncoded(pair.getValue(), StandardCharsets.UTF_8.toString());
            rtn += key + "=" + value + "&";
        }
        rtn = rtn.substring(0, rtn.length() - 1);
        return rtn;
    }

    /**
     * 拼接参数
     *
     * @param paramMap
     */
    public static String addParams2(LinkedHashMap<String, String> paramMap) {
        String rtn = "";
        // 按key排序
        paramMap = HuobiProSignUtil.sort(paramMap);
        /*拼接字符串*/
        for (Map.Entry<String, String> pair : paramMap.entrySet()) {
            rtn += pair.getKey() + "=" + pair.getValue() + "&";
        }
        rtn = rtn.substring(0, rtn.length() - 1);
        return rtn;
    }


    /**
     * url编码
     *
     * @param content
     * @param charset
     * @return
     */
    public static String urlEncoded(String content, String charset) {
        try {
            return URLEncoder.encode(content, charset);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    public static String urlEncoded(String content) {
        try {
            return URLEncoder.encode(content, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("指定的编码集不对,您目前指定的编码集是:" + StandardCharsets.UTF_8);
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
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("指定的编码集不对,您目前指定的编码集是:" + charset);
        }
    }

    public static String urlDecoded(String content) {
        try {
            return URLDecoder.decode(content, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("指定的编码集不对,您目前指定的编码集是:" + StandardCharsets.UTF_8);
        }
    }


    /**
     * 签名
     *
     * @param method       请求方法
     * @param address      ip地址
     * @param resourcePath 资源路径
     * @param paramMap     参数
     * @param secretKey    密钥
     * @return
     */
    public static String sign(String method, String address, String resourcePath,
                              LinkedHashMap<String, String> paramMap, String secretKey) {
        StringBuilder msg = new StringBuilder();
        msg.append(method).append("\n");
        msg.append(address).append("\n");
        msg.append(resourcePath).append("\n");
        String paramStr = HuobiProSignUtil.addParams(paramMap);
        msg.append(paramStr);
        String sign = HuobiProSignUtil.hmacSHA256(msg.toString(), secretKey);
        return sign;
    }

    /**
     * HmacSHA256 加密
     *
     * @param msg       加密内容
     * @param secretKey 私钥
     */
    public static String hmacSHA256(String msg, String secretKey) {
        try {
            Mac sha256_HMAC = Mac.getInstance(SIGNATURE_METHOD);
            SecretKeySpec secret_key = new SecretKeySpec(secretKey.getBytes(), SIGNATURE_METHOD);
            sha256_HMAC.init(secret_key);
            String sign = Base64.encodeBase64String(sha256_HMAC.doFinal(msg.getBytes()));
            return sign;
        } catch (Exception e) {
            System.out.println("Error");
        }
        return null;
    }


    /**
     * 排序
     *
     * @param paramMap   参数
     * @param comparator 比较器
     * @return 本实例对象
     */
    public static LinkedHashMap<String, String> sort(LinkedHashMap<String, String> paramMap, Comparator<String> comparator) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        paramMap.keySet().stream()
                .sorted(comparator)
                .forEach(k -> map.put(k, paramMap.get(k)));
        return map;
    }

    /**
     * 排序
     *
     * @param paramMap 参数
     * @return
     */
    public static LinkedHashMap<String, String> sort(LinkedHashMap<String, String> paramMap) {
        return sort(paramMap, String::compareTo);
    }

    /**
     * 将url参数转换成map
     *
     * @param param aa=11&bb=22&cc=33
     * @return
     */
    public static LinkedHashMap<String, String> getUrlParams(String param) {
        LinkedHashMap<String, String> map = new LinkedHashMap<String, String>(0);
        if ("".equals(param) || param == null) {
            return map;
        }
        String[] params = param.split("&");
        for (int i = 0; i < params.length; i++) {
            String[] p = params[i].split("=");
            if (p.length == 2) {
                map.put(HuobiProSignUtil.urlDecoded(p[0]), HuobiProSignUtil.urlDecoded(p[1]));
            }
        }
        return map;
    }


    /**
     * 获取签名后的请求地址
     * @param method GET/PORT
     * @param protocol 请求协议
     * @param site 网站地址
     * @param resourcePath 资源路径
     * @param accessKey 公钥
     * @param secretKey 密钥
     * @param paramMap 参数
     * @return 请求地址
     */
    private static final String signatureMethod = "SignatureMethod";
    private static final String signatureVersion = "SignatureVersion";
    private static final String signatureVersionValue = "2";
    private static final String timestamp = "Timestamp";
    private static final String signature = "Signature";
    private static final String accessKeyId = "AccessKeyId";
    public static String signRequestUrl(HttpMethod method, String protocol, String site, String resourcePath, String accessKey, String secretKey, LinkedHashMap<String, String> paramMap) {
        // 加密后的拼接参数
        StringBuffer sbParams = HuobiProSignUtil.signRequestParam(method, site, resourcePath, accessKey, secretKey, paramMap);

        // 拼接url
        String url = HuobiProSignUtil.makeUrl(protocol, site, resourcePath, sbParams.toString());
        return url;
    }

    /**
     * 加密后的拼接参数字符串
     * @param method
     * @param site
     * @param resourcePath
     * @param accessKey
     * @param secretKey
     * @param paramMap
     * @return 返回加密后的拼接参数字符串
     */
    public static StringBuffer signRequestParam(HttpMethod method, String site, String resourcePath, String accessKey, String secretKey, LinkedHashMap<String, String> paramMap) {
        paramMap.put(accessKeyId, accessKey);
        paramMap.put(signatureMethod, HuobiProSignUtil.SIGNATURE_METHOD);
        paramMap.put(signatureVersion, signatureVersionValue);
        paramMap.put(timestamp, gmtNow());

        // 获取签名
        String sign = HuobiProSignUtil.sign(method.getValue(), site, resourcePath, paramMap, secretKey);

        // 将sign添加到参数最后
        String params = HuobiProSignUtil.addParams(paramMap);
        StringBuffer sbParams = new StringBuffer();
        sbParams.append(params).append("&").append(signature).append("=").append(HuobiProSignUtil.urlEncoded(sign));

        return sbParams;
    }

    /**
     * 拼接url
     * @param protocol
     * @param site
     * @param resourcePath
     * @param params
     * @return
     */
    public static String makeUrl(String protocol, String site, String resourcePath, String params) {
        StringBuilder url = new StringBuilder();
        url.append(protocol).append(site).append(resourcePath).append("?").append(params);
        return url.toString();
    }

    private static final DateTimeFormatter DT_FORMAT = DateTimeFormatter
            .ofPattern("uuuu-MM-dd'T'HH:mm:ss");
    private static final ZoneId ZONE_GMT = ZoneId.of("Z");
    /**
     * 获取GMT时间
     * @return GMT时间
     */
    static String gmtNow() {
        return Instant.ofEpochSecond(epochNow()).atZone(ZONE_GMT).format(DT_FORMAT);
    }
    private static long epochNow() {
        return Instant.now().getEpochSecond();
    }

    public static void main(String[] args) {
        String appKey = "a4a8e954-14ec032d-78881e2b-xa2b53ggfc";
        String secretKey = "eb42b403-3099e389-c7e54887-e1241";
        String address = "api.huobi.pro";
        /*********下单接口签名*********/
        String resourcePath = "/v1/account/accounts";
        String method = "GET";
        String signatureMethod = "HmacSHA256";
        LinkedHashMap<String, String> paramMap = new LinkedHashMap<>();
        paramMap.put("SignatureMethod", signatureMethod);
        paramMap.put("SignatureVersion", "2");
        paramMap.put("AccessKeyId", appKey);
        paramMap.put("Timestamp", "2019-06-28T10:00:32");
        String sign = HuobiProSignUtil.sign(method, address, resourcePath, paramMap, secretKey);
        System.out.println(sign);

        // 将sign添加到参数最后
        String params = HuobiProSignUtil.addParams(paramMap);
        params += "&Signature=" + HuobiProSignUtil.urlEncoded(sign);
        System.out.println(params);

        // 拼接url
        StringBuilder url = new StringBuilder();
        url.append("https://").append(address).append(resourcePath).append("?").append(params);
        System.out.println(url);
    }
}
