package com.zyf.common.model;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * 基础数据类
 * @author yuanfeng.z
 * @date 2019/10/17 10:20
 */
@Setter
@Getter
public class BaseData implements Serializable {

    protected static final long serialVersionUID = 1L;

    /**
     * 交易所名称
     */
    protected String exchangeName;

    /**
     * 合约id
     */
    protected String instrumentId;

    /**
     * 头部信息
     */
    protected Header header;
}
