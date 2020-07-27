/**
 * 错误码接口
 */
package com.zyf.common.exceptions;

/**
 * 错误码接口
 * @author yuanfeng.z
 * @date 2019/6/25 10:27
 */
public interface ErrorCode {

    /**
     * 获取错误码
     * @return
     */
    String getCode();

    /**
     * 获取错误信息
     * @return
     */
    String getDescription();
}