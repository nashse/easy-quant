package com.zyf.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * 深度档位（价格、数量）
 *
 * @author yuanfeng.z
 * @date 2019/6/25 17:34
 */
@Setter
@Getter
@ToString
@NoArgsConstructor
public class Level implements Serializable {
    private static final long serialVersionUID = -7201349313159184470L;
    /**
     * 价格
     */
    private BigDecimal price;
    /**
     * 数量
     */
    private BigDecimal quantity;

    public Level(BigDecimal price, BigDecimal quantity) {
        this.price = price;
        this.quantity = quantity;
    }
}
