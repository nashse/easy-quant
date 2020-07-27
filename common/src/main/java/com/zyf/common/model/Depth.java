package com.zyf.common.model;

import lombok.*;

import java.util.List;

/**
 * 市场深度类 (添加@Setter和@NoArgsConstructor(force = true)注解，
 * 目的是解决Jackson2JsonMessageConverter序列化、反序列化问题)
 *
 * @author yuanfeng.z
 * @date 2019/6/24 19:58
 */
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class Depth extends BaseData {
    /**
     * 原始数据，以防定义字段不够用
     */
    private String originData;

    /**
     * 时间戳
     */
    private Long timestamp;

    /**
     * 买盘深度
     */
    private List<Level> bids;

    /**
     * 卖盘深度Level
     */
    private List<Level> asks;

    public Depth(String originData, Long timestamp, List<Level> bids, List<Level> asks) {
        this.originData = originData;
        this.timestamp = timestamp;
        this.bids = bids;
        this.asks = asks;
    }

    public Depth(String exchangeName, String instrumentId, String originData, Long timestamp,
                 List<Level> bids, List<Level> asks) {
        this.exchangeName = exchangeName;
        this.instrumentId = instrumentId;
        this.originData = originData;
        this.timestamp = timestamp;
        this.bids = bids;
        this.asks = asks;
    }
}

