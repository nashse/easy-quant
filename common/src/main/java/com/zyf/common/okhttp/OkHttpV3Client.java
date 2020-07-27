package com.zyf.common.okhttp;

import okhttp3.*;

import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * okhttp客户端
 *
 * @author yuanfeng.z
 * @date 2019/6/25 10:27
 */
public class OkHttpV3Client {

    private static OkHttpClient okHttpClient = null;

    /**
     * 实例化OkHttpClient（双重锁饱汉模式）
     *
     * @return OkHttpClient
     */
    private static OkHttpClient getOkHttpClient() {
        if (okHttpClient == null) {
            synchronized (OkHttpV3Client.class) {
                if (okHttpClient == null) {
                    okHttpClient = new OkHttpClient.Builder()
                            //todo 代理，注意上线打包关闭
//                            .proxy(new Proxy(Proxy.Type.HTTP, new InetSocketAddress("127.0.0.1", 1080)))
                            .connectTimeout(5, TimeUnit.SECONDS)
                            .writeTimeout(5, TimeUnit.SECONDS)
                            .readTimeout(5, TimeUnit.SECONDS)
                            .build();
                }
            }
        }
        return okHttpClient;
    }

    public static String get(String url) throws IOException {
        OkHttpClient client = getOkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    public static String get(String url, String headerName, String headerValue) throws IOException {
        OkHttpClient client = getOkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .header(headerName, headerValue)
                .build();
        Response response = client.newCall(request).execute();
        return response.body().string();

    }

    public static String get(String url, Map<String, String> headers) {
        OkHttpClient client = getOkHttpClient();
        Request.Builder builder = new Request.Builder().url(url);
        Request request = addHeader(builder, headers).build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getHeader(String url, LinkedHashMap<String, String> lh) {

        OkHttpClient client = getOkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("OK-ACCESS-KEY", lh.get("OK-ACCESS-KEY"))
                .addHeader("OK-ACCESS-SIGN", lh.get("OK-ACCESS-SIGN"))
                .addHeader("OK-ACCESS-TIMESTAMP", lh.get("OK-ACCESS-TIMESTAMP"))
                .addHeader("OK-ACCESS-PASSPHRASE", lh.get("OK-ACCESS-PASSPHRASE"))
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String getHeaderEmex(String url, LinkedHashMap<String, String> lh) {

        OkHttpClient client = getOkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("Content-Type", "application/json")
                .addHeader("ACCESS-KEY", lh.get("api_key"))
                .addHeader("ACCESS-SIGN", lh.get("sign"))
                .addHeader("ACCESS-TIMESTAMP", lh.get("time"))
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String postHeader(String url, LinkedHashMap<String, String> lh, String data) {
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);

        OkHttpClient client = getOkHttpClient();
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader("OK-ACCESS-KEY", lh.get("OK-ACCESS-KEY"))
                .addHeader("OK-ACCESS-SIGN", lh.get("OK-ACCESS-SIGN"))
                .addHeader("OK-ACCESS-TIMESTAMP", lh.get("OK-ACCESS-TIMESTAMP"))
                .addHeader("OK-ACCESS-PASSPHRASE", lh.get("OK-ACCESS-PASSPHRASE"))
                .build();
        try (Response response = client.newCall(request).execute()) {
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String post(String url, String data) {
        OkHttpClient client = getOkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder()
                .url(url)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String post(String url, String data, String headerName, String headerValue) {
        OkHttpClient client = getOkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Request request = new Request.Builder()
                .url(url)
                .header(headerName, headerValue)
                .post(body)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String post(String url, String data, Map<String, String> headers) {
        OkHttpClient client = getOkHttpClient();
        MediaType JSON = MediaType.parse("application/json; charset=utf-8");
        RequestBody body = RequestBody.create(JSON, data);
        Request.Builder builder = new Request.Builder().url(url);
        Request request = addHeader(builder, headers).post(body).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String delete(String url) {
        OkHttpClient client = getOkHttpClient();
        FormBody formBody = new FormBody.Builder().build();
        Request request = new Request.Builder().url(url).delete(formBody).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String delete(String url, String headerName, String headerValue) {
        OkHttpClient client = getOkHttpClient();
        FormBody formBody = new FormBody.Builder().build();
        Request request = new Request.Builder()
                .url(url)
                .addHeader(headerName, headerValue)
                .delete(formBody)
                .build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String delete(String url, Map<String, String> headers) {
        OkHttpClient client = getOkHttpClient();
        FormBody formBody = new FormBody.Builder().build();
        Request.Builder builder = new Request.Builder().url(url);
        Request request = addHeader(builder, headers).delete(formBody).build();
        try {
            Response response = client.newCall(request).execute();
            return response.body().string();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static Request.Builder addHeader(Request.Builder builder, Map<String, String> headers) {
        if (headers != null && !headers.isEmpty()) {
            Iterator<Map.Entry<String, String>> iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<String, String> entry = iterator.next();
                builder.addHeader(entry.getKey(), entry.getValue());
            }
        }
        return builder;
    }

}
