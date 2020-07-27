package com.zyf.common.okhttp;

import com.zyf.common.exceptions.ExTimeOutException;
import com.zyf.common.http.HttpUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * okhttpV3代理类（用于处理业务异常）
 *
 * @author yuanfeng.z
 * @date 2019/7/11 15:51
 */
@Slf4j
public class OkHttpV3ClientProxy {
    private static OkHttpV3ClientProxy okHttpV3ClientProxy = null;

    public static OkHttpV3ClientProxy getOkHttpV3ClientProxy() {
        if (okHttpV3ClientProxy == null) {
            synchronized (OkHttpV3ClientProxy.class) {
                if (okHttpV3ClientProxy == null) {
                    okHttpV3ClientProxy = new OkHttpV3ClientProxy();
                }
            }
        }
        return okHttpV3ClientProxy;
    }

    public static String get(String url) {
        try {
            return OkHttpV3Client.get(url);
        } catch (SocketTimeoutException | ConnectException e) {
            throw new ExTimeOutException("connect exchange time out");
        } catch (IOException e) {
            e.printStackTrace();
        }
            return null;
        }

    public static String post(String url, HashMap<String, String> paramsMap) {
        try {
            return HttpUtil.doPost(url,paramsMap);
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    public static String get(String url, String headerName, String headerValue) {
        try {
            return OkHttpV3Client.get(url, headerName, headerValue);
        } catch (SocketTimeoutException | ConnectException e) {
            throw new ExTimeOutException("connect exchange time out");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String get(String url, Map<String, String> headers) {
        return OkHttpV3Client.get(url, headers);
    }

    public static String getHeader(String url, LinkedHashMap<String, String> lh) {
        return OkHttpV3Client.getHeader(url, lh);
    }

    public static String postHeader(String url, LinkedHashMap<String, String> lh, String data) {
        return OkHttpV3Client.postHeader(url, lh, data);
    }

    public static String post(String url, String data) {
        return OkHttpV3Client.post(url, data);
    }

    public static String post(String url, String data, String headerName, String headerValue) {
        return OkHttpV3Client.post(url, data, headerName, headerValue);
    }

    public static String post(String url, String data, Map<String, String> headers) {
        return OkHttpV3Client.post(url, data, headers);
    }

    public static String delete(String url) {
        return OkHttpV3Client.delete(url);
    }

    public static String delete(String url, String headerName, String headerValue) {
        return OkHttpV3Client.delete(url, headerName, headerValue);
    }

    public static String delete(String url, Map<String, String> headers) {
        return OkHttpV3Client.delete(url, headers);
    }
}
