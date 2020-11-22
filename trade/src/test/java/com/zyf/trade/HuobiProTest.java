package com.zyf.trade;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.zyf.common.model.Order;
import com.zyf.common.model.Trade;
import com.zyf.trade.http.option.HuobiProTradeOptionExchange;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public class HuobiProTest {
    public static void main(String[] args) {
        HuobiProTradeOptionExchange huobiProTradeOptionExchange =
                new HuobiProTradeOptionExchange("xxx-xxx-xxx-xx",
                        "xx-xx-xx-xx");
        List<Order> lists  = huobiProTradeOptionExchange.getOrders("BTC");
        List<Trade> trades = new ArrayList<>();
        for (Order order : lists) {
            JSONObject json = JSON.parseObject(order.getOriginData());
            Long time = order.getTimestamp();
            String orderType = json.getString("order_type");
            StringBuilder sb = new StringBuilder();
            sb.append(order.getOrderId())
                    .append("/")
                    .append(time)
                    .append("/")
                    .append(orderType);
            trades.addAll(huobiProTradeOptionExchange.getTrades("BTC", sb.toString()));
            log.info(trades.toString());
        }
log.info("" +trades.size());
    }
}
