package com.zyf.common.model.option;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

import java.math.BigDecimal;

/**
 * 期权合约详细类
 * @author yuanfeng.z
 * @date 2020/9/14 22:50
 */
@Data
@AllArgsConstructor
public class OptionDetailInfo {
    private String originData;
    private String symbol;
    private String tradePartition;
    private String contractType;
    private String optionRightType;
    private String contractCode;
    private BigDecimal ivLastPrice;
    private BigDecimal ivAskOne;
    private BigDecimal ivBidOne;
    private BigDecimal ivMarkPrice;
    private BigDecimal delta;
    private BigDecimal gamma;
    private BigDecimal theta;
    private BigDecimal vega;
    private BigDecimal askOne;
    private BigDecimal bidOne;
    private BigDecimal lastPrice;
    private BigDecimal markPrice;
}
