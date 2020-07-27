package com.zyf.common.exceptions;

/**
 * 交易所连接超过异常
 * @author yuanfeng.z
 * @date 2019/7/11 15:40
 */
public class ExTimeOutException extends RuntimeException {
    public ExTimeOutException(String msg) {
        super(msg);
    }
}
