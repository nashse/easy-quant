package com.zyf.strategy;

import com.alibaba.fastjson.JSONObject;
import com.zyf.common.model.Depth;
import com.zyf.common.model.Order;
import com.zyf.common.model.Position;
import com.zyf.common.model.enums.Side;
import com.zyf.common.util.CommomUtil;
import com.zyf.common.util.MathUtil;
import com.zyf.common.util.RepeatUtil;
import com.zyf.framework.boot.BaseStrategy;
import com.zyf.framework.scheduling.interfaces.ScheduledByZyf;
import com.zyf.framework.service.impl.EmailServiceImpl;
import com.zyf.framework.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static java.math.BigDecimal.*;

/**
 * okex网格策略
 * <p>
 * https://www.jianshu.com/p/fb48c2fb699b
 * <p>
 * 止损一次40usdt，止盈一次1usdt，在网格内交易40次以上才有盈利。
 * 概率：震荡行情概率大。
 * 等比：上涨盈利比亏损更多；下跌亏损比盈利更多。
 * 此策略在震荡上涨行情中，有优势。
 *
 * @author yuanfeng.z
 * @date 2020/7/27 1:44
 */
@Slf4j
public class OkexGridStrategy extends BaseStrategy {

    /**
     * 每格利润
     */
    private BigDecimal spreadRate = new BigDecimal("0.003");

    /**
     * btc-usdt合约最小单位
     */
    private BigDecimal minUtil = new BigDecimal("0.1");

    /**
     * 当前格价格
     */
    private BigDecimal currGridPrice = new BigDecimal("0");

    /**
     * 单边格子数量（-5 -4 -3 -2 -1 0 1 2 3 4 5）
     * <br/> 实盘不一定是10，有可能大于或小于
     */
    private Integer gridNum = 7;

    /**
     * 网格最低价
     */
    private BigDecimal lowPrice = ZERO;

    /**
     * 网格最高价
     */
    private BigDecimal higtPrice = ZERO;

    /**
     * 杠杆
     */
    private BigDecimal leverage = new BigDecimal("40");

    /**
     * 每单数量 (1相当于okex usdt本位永续合约的0.01)
     */
    private BigDecimal quantity = new BigDecimal("5");

    /**
     * 发邮件配置
     */
    private String host = "smtpdm.aliyun.com";
    private String fromMail = "1004283115@qq.com";
    private String user = "email@algotrade.cc";
    private String password = "ZHIDUOduo2020";
    private String toMail = "1004283115@qq.com";
    private String mailTitle = "etwet";
    private String mailContent = "fssd";

    @Override
    protected void init() {
        log.info("init");

        tradeE.setLeverage(symbol, leverage);

        // 撤掉所有订单
        this.cancelAll();

        Depth d = mdE.getDepth(symbol);
        BigDecimal askPrice = d.getAsks().get(0).getPrice();
        BigDecimal bidPrice = d.getBids().get(0).getPrice();
        BigDecimal midPrice = askPrice.add(bidPrice).
                divide(new BigDecimal("2")).
                setScale(1, RoundingMode.DOWN);
        currGridPrice = midPrice;

        lowPrice = midPrice.multiply(BigDecimal.ONE.subtract(new BigDecimal(gridNum.toString()).multiply(spreadRate)));
        higtPrice = midPrice.multiply(BigDecimal.ONE.add(new BigDecimal(gridNum.toString()).multiply(spreadRate)));

        CommomUtil.repeatOrderMarket(tradeE, "openLongMarket", "-1", 5,
                symbol, quantity, -1);
        CommomUtil.repeatOrderMarket(tradeE, "openShortMarket", "-1", 5,
                symbol, quantity, -1);
    }

    @Override
    protected void recovery() {
        log.info("recovery");
        String content = FileUtil.readJsonFile(storeFile);
        JSONObject json = JSONObject.parseObject(content);
        currGridPrice = json.getBigDecimal("currGridPrice");
        lowPrice = json.getBigDecimal("lowPrice");
        higtPrice = json.getBigDecimal("higtPrice");
    }

    @Override
    @ScheduledByZyf(cron = "*/1 * * * * ?")
    protected void run() {
        Depth d = mdE.getDepth(symbol);
        BigDecimal askPrice = d.getAsks().get(0).getPrice();
        BigDecimal bidPrice = d.getBids().get(0).getPrice();
        BigDecimal midPrice = askPrice.add(bidPrice).
                divide(new BigDecimal("2")).
                setScale(1, RoundingMode.DOWN);

        try {
            // 行情下跌
            BigDecimal downPrice = currGridPrice.multiply(ONE.subtract(spreadRate)).setScale(1, RoundingMode.UP);
            downPrice = MathUtil.adjustMinUtil(downPrice, minUtil, RoundingMode.UP);
            if (askPrice.compareTo(downPrice) == -1) {
                log.info("行情下跌 --> askPrice：{} downPrice：{} currGridPrice：{}", askPrice, downPrice, currGridPrice);

                // 小于最低价
                if (askPrice.compareTo(lowPrice) == -1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("下跌行情，止损 --> askPrice：").
                            append(askPrice).
                            append(" lowPrice：").
                            append(lowPrice);
                    log.info(sb.toString());
                    this.closePositions();
                    this.init();
                    this.sendEmail("止损", sb.toString());
                    return;
                }

                String id = null;
                // 平空仓
                id = CommomUtil.repeatOrderMarket(tradeE, "closeShortMarket", "-1", 5,
                        symbol, quantity, -1);
                if ("-1".equals(id)) {
                    this.setCurrGridPrice(midPrice);
                    StringBuilder sb = new StringBuilder();
                    sb.append("下跌行情，closeShortMarket 失败 --> quantity：").
                            append(quantity);
                    log.error(sb.toString());
                    this.sendEmail("平空仓失败", sb.toString());
                    return;
                }
                log.info("下跌行情 --> 平空仓，平仓数量：{}", quantity);

                // 下跌行情，每个格子都开多
                id = CommomUtil.repeatOrderMarket(tradeE, "openLongMarket", "-1", 5,
                        symbol, quantity, -1);
                if ("-1".equals(id)) {
                    this.setCurrGridPrice(midPrice);
                    StringBuilder sb = new StringBuilder();
                    sb.append("下跌行情，openLongMarket 失败 --> quantity：").
                            append(quantity);
                    log.error(sb.toString());
                    this.sendEmail("开多失败", sb.toString());
                    return;
                }
                log.info("下跌行情 --> 开多");

                // 开仓前先获取当前仓位
                Boolean b = false;
                List<Position> positions = tradeE.getPositions(symbol);
                for (Position p : positions) {
                    if (Side.OPEN_SHORT.equals(p.getSide())) {
                        b = true;
                    }
                }
                if (!b) {
                    // 保持有空仓位
                    id = CommomUtil.repeatOrderMarket(tradeE, "openShortMarket", "-1", 5,
                            symbol, quantity, -1);
                    if ("-1".equals(id)) {
                        this.setCurrGridPrice(midPrice);
                        StringBuilder sb = new StringBuilder();
                        sb.append("下跌行情，openLongMarket 失败 --> quantity：").
                                append(quantity);
                        log.error(sb.toString());
                        this.sendEmail("保持有空仓位失败", sb.toString());
                        return;
                    }
                }
                log.info("下跌行情 --> 保持有空仓位");

                this.setCurrGridPrice(midPrice);
            }

            // 行情上升
            BigDecimal upPrice = currGridPrice.multiply(ONE.add(spreadRate)).setScale(1, RoundingMode.UP);
            upPrice = MathUtil.adjustMinUtil(upPrice, minUtil, RoundingMode.UP);
            if (bidPrice.compareTo(upPrice) == 1) {
                log.info("行情上升 --> bidPrice：{} upPrice：{} currGridPrice：{}", bidPrice, upPrice, currGridPrice);

                // 大于最高价
                if (bidPrice.compareTo(higtPrice) == 1) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("下跌行情，止损 --> askPrice：").
                            append(askPrice).
                            append(" lowPrice：").
                            append(lowPrice);
                    log.info(sb.toString());
                    this.closePositions();
                    this.init();
                    this.sendEmail("止损", sb.toString());
                    return;
                }

                String id = null;
                // 平多仓
                id = CommomUtil.repeatOrderMarket(tradeE, "closeLongMarket", "-1", 5,
                        symbol, quantity, -1);
                if ("-1".equals(id)) {
                    this.setCurrGridPrice(midPrice);
                    StringBuilder sb = new StringBuilder();
                    sb.append("行情上升，closeLongMarket 失败 --> quantity：").
                            append(quantity);
                    log.error(sb.toString());
                    this.sendEmail("平多仓失败", sb.toString());
                    return;
                }
                log.info("行情上升 --> 平多仓，平仓数量：{}", quantity);

                // 行情上升，每个格子都开空
                id = CommomUtil.repeatOrderMarket(tradeE, "openShortMarket", "-1", 5,
                        symbol, quantity, -1);
                if ("-1".equals(id)) {
                    this.setCurrGridPrice(midPrice);
                    StringBuilder sb = new StringBuilder();
                    sb.append("行情上升，openShortMarket 失败 --> quantity：").
                            append(quantity);
                    log.error(sb.toString());
                    this.sendEmail("开空失败", sb.toString());
                    return;
                }
                log.info("行情上升 --> 开空");

                // 开仓前先获取当前仓位
                Boolean b = false;
                List<Position> positions = tradeE.getPositions(symbol);
                for (Position p : positions) {
                    if (Side.OPEN_LONG.equals(p.getSide())) {
                        b = true;
                    }
                }
                if (!b) {
                    // 保持有多仓位
                    id = CommomUtil.repeatOrderMarket(tradeE, "openLongMarket", "-1", 5,
                            symbol, quantity, -1);
                    if ("-1".equals(id)) {
                        this.setCurrGridPrice(midPrice);
                        StringBuilder sb = new StringBuilder();
                        sb.append("行情上升，openLongMarket 失败 --> quantity：").
                                append(quantity);
                        log.error(sb.toString());
                        this.sendEmail("保持有多仓位失败", sb.toString());
                        return;
                    }
                }
                log.info("行情上升 --> 保持有空仓位");

                this.setCurrGridPrice(midPrice);
            }

        } catch (Exception e) {
            this.setCurrGridPrice(midPrice);
            log.error(e.toString());
            this.sendEmail("异常", e.toString());
        }
    }

    /**
     * 设置当前网格价格
     *
     * @param midPrice
     */
    private void setCurrGridPrice(BigDecimal midPrice) {
        currGridPrice = midPrice;
        log.info("新当前网格行情中间 --> currGridPrice：{}", currGridPrice);
    }

    @ScheduledByZyf(cron = "* */1 * * * ?")
    private void persistent() {
        JSONObject json = new JSONObject();
        json.put("currGridPrice", currGridPrice);
        json.put("lowPrice", lowPrice);
        json.put("higtPrice", higtPrice);
        FileUtil.writeJsonFile(storeFile, json.toJSONString());
    }

    /**
     * 撤销所有订单
     */
    private void cancelAll() {
        // 撤销委托单
        List<Order> poList = tradeE.getPendingOrders(symbol);
        log.info(poList.toString());
        List<String> ids = poList.stream().map(Order::getOrderId).collect(Collectors.toList());
        Boolean b = tradeE.cancelOrders(symbol, ids);
        if (!b) {
            log.info("撤销委托单失败，order：{}", poList);
        }

        // 撤销计划订单
        List<Order> tpoList = tradeE.getTriggerPendingOrders(symbol);
        ids = tpoList.stream().map(Order::getOrderId).collect(Collectors.toList());
        log.info(tpoList.toString());
        tradeE.triggerCancelOrders(symbol, ids);
        if (!b) {
            log.info("撤销计划订单，order：{}", tpoList);
        }
    }

    /**
     * 平所有仓位
     */
    private void closePositions() {
        List<Position> positions = RepeatUtil.repeat(symbol, tradeE::getPositions, 5);
        for (Position p : positions) {
            if (Side.OPEN_LONG.equals(p.getSide())) {
                CommomUtil.repeatOrderMarket(tradeE, "closeLongMarket", -1, 5,
                        symbol, p.getAvailQuantity(), -1);
            } else if (Side.OPEN_SHORT.equals(p.getSide())) {
                CommomUtil.repeatOrderMarket(tradeE, "closeSellMarket", -1, 5,
                        symbol, p.getAvailQuantity(), -1);
            }
        }
    }

    /**
     * 发邮件
     *
     * @param title   标题
     * @param content 内容
     */
    private void sendEmail(String title, String content) {
        StringBuilder emailTitle = new StringBuilder();
        emailTitle.append("okex合约策略网格：").append(title);
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("交易所：").append(exchangeName);
        emailContent.append("币对：").append(symbol);
        EmailServiceImpl.sendMail(host, user, password, fromMail, toMail, title, content);
    }
}
