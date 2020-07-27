package com.zyf.common.model.enums;

/**
 * 订单状态
 * @author yuanfeng.z
 * @date 2019/6/29 11:06
 */
public enum State {

    /**
     * 已提交
     */
    SUBMIT(0),
    /**
     * 已成交
     */
    FILLED(1),
    /**
     * 已取消
     */
    CANCEL(2),
    /**
     * 部分成交
     */
    PARTIAL(3),
    /**
     * 订单被拒绝
     */
    REJECTED(4),
    /**
     * 止损单
     */
    STOP(5);;

    private Integer value;

    State(Integer value) {
        this.value = value;
    }

    public Integer getValue() {
        return this.value;
    }
}
