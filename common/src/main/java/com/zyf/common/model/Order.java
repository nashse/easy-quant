package com.zyf.common.model;

import com.zyf.common.model.enums.Side;
import com.zyf.common.model.enums.State;
import com.zyf.common.model.enums.Type;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Objects;

/**
 * 订单类
 *
 * @author yuanfeng.z
 * @date 2019/6/29 10:14
 */
@Getter
@ToString
@Data
@EqualsAndHashCode
public class Order implements Serializable {
    private static final long serialVersionUID = -8129844919458631192L;

    /**
     * 原始数据，以防定义字段不够用
     */
    private final String originData;

    /**
     * 时间戳
     */
    private final Long timestamp;

    /**
     * 订单id
     */
    private final String orderId;

    /**
     * 价格
     */
    private final BigDecimal price;

    /**
     * 数量
     */
    private final BigDecimal quantity;

    /**
     * 成交数量
     */
    private final BigDecimal deal;

    /**
     * 买卖方向
     */
    private final Side side;

    /**
     * 类型（限价、市价等）
     */
    private final Type type;

    /**
     * 状态（全部成交、未成交等）
     */
    private final State state;


    public Order(String originData, Long timestamp, String orderId, BigDecimal price, BigDecimal quantity, BigDecimal deal, Side side, Type type, State state) {
        this.originData = originData;
        this.timestamp = timestamp;
        this.orderId = orderId;
        this.price = price;
        this.quantity = quantity;
        this.deal = deal;
        this.side = side;
        this.type = type;
        this.state = state;
    }

    public Order(String orderId, BigDecimal price, BigDecimal quantity, Side side) {
        this.originData = null;
        this.timestamp = null;
        this.orderId = orderId;
        this.price = price;
        this.quantity = quantity;
        this.deal = null;
        this.side = side;
        this.type = null;
        this.state = null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Order order = (Order) o;
        return Objects.equals(orderId, order.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(originData, timestamp, orderId, price, quantity, deal, side, type, state);
    }
}
