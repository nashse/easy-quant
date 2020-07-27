package com.zyf.common.model.http;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;

/**
 * HTTP请求参数
 *
 * @author yuanfeng.z
 * @date 2019-06-27
 */
public class HttpParameter extends LinkedHashMap<String, Object> implements Serializable {

    private HttpParameter() {
    }

    /**
     * 构造器
     *
     * @return 实例对象
     */
    public static HttpParameter build() {
        return new HttpParameter();
    }

    /**
     * 构造器
     *
     * @param key   键
     * @param value 值
     * @return 实例对象
     */
    public static HttpParameter build(String key, Object value) {
        HttpParameter parameter = new HttpParameter();
        parameter.put(key, value);
        return parameter;
    }

    /**
     * 添加键值对
     *
     * @param key   键
     * @param value 值
     * @return 本实例对象
     */
    public HttpParameter add(String key, Object value) {
        super.put(key, value);
        return this;
    }

    /**
     * 排序
     *
     * @param comparator 比较器
     * @return 本实例对象
     */
    public HttpParameter sort(Comparator<String> comparator) {
        HttpParameter parameter = new HttpParameter();
        this.keySet().stream()
                .sorted(comparator)
                .forEach(k -> parameter.add(k, this.get(k)));
        this.clear();
        parameter.forEach(this::add);
        return this;
    }

    /**
     * 排序，默认按字符串ASCII排序
     *
     * @return 本实例对象
     */
    public HttpParameter sort() {
        return sort(String::compareTo);
    }

    /**
     * 连接
     *
     * @param delimiter 键和值之间的符号
     * @param join      每对键值之间的符号
     * @param encode    编码函数
     * @return 请求参数连接结果
     */
    public String concat(String delimiter, String join, Function<Object, String> encode) {
        Objects.requireNonNull(delimiter, "delimiter");
        Objects.requireNonNull(join, "join");
        if (null == encode) {
            encode = Object::toString;
        }
        StringBuilder sb = new StringBuilder();
        Function<Object, String> finalEncode = encode;
        this.forEach((k, v) -> sb.append(k).append(delimiter)
                .append(finalEncode.apply(v)).append(join));
        String result = sb.toString();
        if (0 < sb.length()) {
            result = result.substring(0, result.length() - join.length());
        }
        return result;
    }

    /**
     * 连接
     *
     * @param encode 编码函数
     * @return 连接结果
     */
    public String concat(Function<Object, String> encode) {
        return concat("=", "&", encode);
    }

    /**
     * 连接
     *
     * @return 连接结果
     */
    public String concat() {
        return concat("=", "&", null);
    }

    /**
     * 转换json格式
     *
     * @param function 转换函数
     * @return json字符串对象
     */
    public String json(Function<Map, String> function) {
        return function.apply(this);
    }

    /**
     * 转换json格式
     *
     * @return
     */
    public String json() {
        return JSONObject.toJSONString(this);
    }

}
