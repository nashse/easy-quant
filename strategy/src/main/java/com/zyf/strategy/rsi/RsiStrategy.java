package com.zyf.strategy.rsi;

import com.alibaba.fastjson.JSONObject;
import com.tictactec.ta.lib.Core;
import com.tictactec.ta.lib.MInteger;
import com.tictactec.ta.lib.RetCode;
import com.zyf.common.model.*;
import com.zyf.common.model.enums.Side;
import com.zyf.common.util.CommomUtil;
import com.zyf.common.util.IndexUtil;
import com.zyf.common.util.MathUtil;
import com.zyf.framework.boot.BaseStrategy;
import com.zyf.framework.scheduling.interfaces.ScheduledByZyf;
import com.zyf.framework.service.impl.EmailServiceImpl;
import com.zyf.framework.util.FileUtil;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static java.math.BigDecimal.*;

/**
 * 以rsi为核心的策略，
 * 使用跟踪止盈方式
 *
 * @author yuanfeng.z
 * @date 2020/8/25 12:14
 */
@Slf4j
public class RsiStrategy extends BaseStrategy {

    /**
     * rsi下界
     */
    private Double lowLimit = 30d;
    /**
     * rsi上界
     */
    private Double highLimit = 70d;
    /**
     * rsi低界限状态
     */
    private RsiState lowRsiState = new RsiState();

    /**
     * rsi高界限状态
     */
    private RsiState highRsiState = new RsiState();

    /**
     * btc-usdt合约最小单位
     */
    private BigDecimal minUtil = new BigDecimal("0.1");

    /**
     * 每单数量 (1相当于okex usdt本位永续合约的0.01)
     */
    private BigDecimal quantity = new BigDecimal("1");

    /**
     * 止损比例
     */
    private BigDecimal stopLossRate = new BigDecimal("0.003");

    /**
     * 止盈比例
     */
    private BigDecimal stopProfitRate = new BigDecimal("0.003");

    /**
     * 跟踪止盈的上一次价格
     */
    private BigDecimal basePrice = ZERO;

    /**
     * 倒数器
     */
    private Timer timer = new Timer();

    /**
     * 杠杆
     */
    private BigDecimal leverage = new BigDecimal("40");

    /**
     * 倒数计数器（分钟）
     */
    private int count = 10;


    /**
     * 发邮件配置
     */
    private String host = "smtpdm.aliyun.com";
    private String fromMail = "email@algotrade.cc";
    private String user = "email@algotrade.cc";
    private String password = "ZHIDUOduo2020";
    private String toMail = "1004283115@qq.com";

    @Override
    protected void init() {
        tradeE.setLeverage(symbol, leverage);
        this.initLog();
    }

    @Override
    protected void recovery() {
        tradeE.setLeverage(symbol, leverage);

        String content = FileUtil.readJsonFile(storeFile);
        JSONObject json = JSONObject.parseObject(content);
        basePrice = json.getBigDecimal("basePrice");

        this.initLog();
    }

    public void initLog() {
        log.info("止盈比例:{}", stopProfitRate);
        log.info("下单数量:{}", quantity);
        log.info("合约最小单位:{}", minUtil);
        log.info("rsi下界:{}", lowLimit);
        log.info("rsi上界:{}", highLimit);
        log.info("basePrice:{}", basePrice);
        log.info("倒数计数器：{}", count);
    }

    @Override
    @ScheduledByZyf(cron = "*/1 * * * * ?")
    protected void run() {
        this.rsiSignal();

        try {
            // 下限
            if (RsiState.State.FINISH.equals(lowRsiState.getState())) {
                if (!timer.bEnd()) {
                    return;
                }

                if (this.bPosition()) {
                    return;
                }
                // 清理之前的平仓止盈止损单，如果没有持仓，执行平仓没影响
                this.closeHandle();

                log.info("做多");
                id = CommomUtil.repeatOrderMarket(tradeE, "openLongMarket", "-1", 5,
                        symbol, quantity, -1);
                if ("-1".equals(id)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("openLongMarket 失败 --> quantity：").
                            append(quantity);
                    log.error(sb.toString());
                    this.sendEmail("开多仓失败", sb.toString());
                    return;
                }

//                BigDecimal dealPrice = this.getDealPrice(id);
//                basePrice = dealPrice;
//                log.info("basePrice：{}", basePrice);
//                BigDecimal lossPrice = MathUtil.adjustMinUtil(basePrice.multiply(ONE.subtract(stopProfitRate)), minUtil, RoundingMode.UP);
//                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseLong", "-1", 5,
//                        symbol, lossPrice, quantity, -1);
//                log.info("平多止损价：{}", lossPrice);

//                BigDecimal dealPrice = this.getDealPrice(id);
//                log.info("跟踪止损价格：{}", dealPrice);
//                BigDecimal lossPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.subtract(stopLossRate)), minUtil, RoundingMode.UP);
//                CommomUtil.repeatOrderTracking(tradeE, "trackingCloseLong", "-1", 5,
//                        symbol, dealPrice, quantity, stopLossRate);

                BigDecimal dealPrice = this.getDealPrice(id);
                log.info("开仓价格：{}", dealPrice);
                BigDecimal lossPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.subtract(stopLossRate)), minUtil, RoundingMode.UP);
                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseLong", "-1", 5,
                        symbol, lossPrice, quantity, -1);
                log.info("止损价：{}", lossPrice);

                BigDecimal profitPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.add(stopProfitRate)), minUtil, RoundingMode.UP);
                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseLong", "-1", 5,
                        symbol, profitPrice, quantity, -1);
                log.info("止盈价：{}", profitPrice);

                timer.start(count);
                lowRsiState.setState(RsiState.State.PREPARE);
            }

            if (RsiState.State.FINISH.equals(highRsiState.getState())) {
                if (!timer.bEnd()) {
                    return;
                }

                if (this.bPosition()) {
                    return;
                }
                // 清理之前的平仓止盈止损单，如果没有持仓，执行平仓没影响
                this.closeHandle();

                log.info("做空");
                id = CommomUtil.repeatOrderMarket(tradeE, "openShortMarket", "-1", 5,
                        symbol, quantity, -1);
                if ("-1".equals(id)) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("openShortMarket 失败 --> quantity：").
                            append(quantity);
                    log.error(sb.toString());
                    this.sendEmail("开空仓失败", sb.toString());
                    return;
                }

//                BigDecimal dealPrice = this.getDealPrice(id);
//                basePrice = dealPrice;
//                log.info("basePrice：{}", basePrice);
//                BigDecimal lossPrice = MathUtil.adjustMinUtil(basePrice.multiply(ONE.add(stopProfitRate)), minUtil, RoundingMode.UP);
//                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseShort", "-1", 5,
//                        symbol, lossPrice, quantity, -1);
//                log.info("平空止损价：{}", lossPrice);

//                BigDecimal dealPrice = this.getDealPrice(id);
//                log.info("跟踪止损价格：{}", dealPrice);
//                BigDecimal lossPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.add(stopLossRate)), minUtil, RoundingMode.UP);
//                CommomUtil.repeatOrderTracking(tradeE, "trackingCloseShort", "-1", 5,
//                        symbol, dealPrice, quantity, stopLossRate);

                BigDecimal dealPrice = this.getDealPrice(id);
                log.info("开仓价格：{}", dealPrice);
                BigDecimal lossPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.add(stopLossRate)), minUtil, RoundingMode.UP);
                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseShort", "-1", 5,
                        symbol, lossPrice, quantity, -1);
                log.info("止损价：{}", lossPrice);

                BigDecimal profitPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.subtract(stopProfitRate)), minUtil, RoundingMode.DOWN);
                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseShort", "-1", 5,
                        symbol, profitPrice, quantity, -1);
                log.info("止盈价：{}", profitPrice);

                timer.start(count);
                highRsiState.setState(RsiState.State.PREPARE);
            }
        } catch (Exception e) {
            // todo 发邮件
            this.sendEmail("error", e.toString());
            e.printStackTrace();
            log.error(e.toString());
        }

    }

    /**
     * 跟踪止盈
     */
//    @ScheduledByZyf(cron = "*/1 * * * * ?")
    public void trackingProfit() {
        // 获取持仓
        List<Position> positions = tradeE.getPositions(symbol);
        if (positions.size() == 0) {
            return;
        }
        if (positions.size() > 1) {
            throw new RuntimeException("持仓 > 1");
        }
        Position p = positions.get(0);

        BigDecimal ask1 = getAsk1();
        BigDecimal bid1 = getBid1();

        Side side = p.getSide();
        if (Side.OPEN_SHORT.equals(side)) {
            if (ask1.compareTo(basePrice.multiply(ONE.subtract(stopProfitRate))) == -1) {
                basePrice = ask1;
                log.info("basePrice：{}", ask1);
                BigDecimal lossPrice = MathUtil.adjustMinUtil(ask1.multiply(ONE.add(stopProfitRate)), minUtil, RoundingMode.UP);
                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseShort", "-1", 5,
                        symbol, lossPrice, quantity, -1);
                log.info("平空止损价：{}", lossPrice);
            }
        } else if (Side.OPEN_LONG.equals(side)) {
            if (bid1.compareTo(basePrice.multiply(ONE.add(stopProfitRate))) == 1) {
                basePrice = bid1;
                log.info("basePrice：{}", bid1);
                BigDecimal lossPrice = MathUtil.adjustMinUtil(bid1.multiply(ONE.subtract(stopProfitRate)), minUtil, RoundingMode.UP);
                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseLong", "-1", 5,
                        symbol, lossPrice, quantity, -1);
                log.info("平多止损价：{}", lossPrice);

            }

        } else {
            log.error("side is error, positions: {}", positions.toString());
            throw new RuntimeException("side is error, positions: " + positions.toString());
        }

    }

    @ScheduledByZyf(cron = "0 */1 * * * ?")
    private void persistent() {
        JSONObject json = new JSONObject();
        json.put("basePrice", basePrice);
        FileUtil.writeJsonFile(storeFile, json.toJSONString());
        log.info("持久化：{}", json.toJSONString());
    }

    /**
     * rsi信号
     */
    private void rsiSignal() {
        final String granularity = "60";
        final int size = 200;
        // 天数
        final int N = 14;
        List<Kline> klines = mdE.getKline(symbol, granularity);
        klines = klines.subList(0, size);
        BarSeries barSeries = IndexUtil.toBarSeriesSame(klines, granularity);

        RSIIndicator rsiIndicator = new RSIIndicator(new ClosePriceIndicator(barSeries), N);
        log.info("rsi指标值 : " + rsiIndicator.getValue(size - 1));
        Num num = rsiIndicator.getValue(size - 1);

        if (lowRsiState.getState().equals(RsiState.State.PREPARE) &&
                (num.doubleValue() < lowLimit)) {
            lowRsiState.setState(RsiState.State.MIDDLE);

        }
        if (lowRsiState.getState().equals(RsiState.State.MIDDLE) &&
                (num.doubleValue() > lowLimit)) {
            lowRsiState.setState(RsiState.State.FINISH);
            log.info("lowRsiState is finish, rsiValue：{}", num);
        }

        if (highRsiState.getState().equals(RsiState.State.PREPARE) &&
                (num.doubleValue() > highLimit)) {
            highRsiState.setState(RsiState.State.MIDDLE);
        }
        if (highRsiState.getState().equals(RsiState.State.MIDDLE) &&
                (num.doubleValue() < highLimit)) {
            highRsiState.setState(RsiState.State.FINISH);
            log.info("highRsiState is finish, rsiValue：{}", num);
        }
    }

    private void rsiSignalTaLib() {
        final String granularity = "60";
        final int size = 200;
        List<Kline> klines = mdE.getKline(symbol, granularity);
        klines = klines.subList(0, size);

        double[] closePrice = new double[size];
        double[] out = new double[size];
        MInteger begin = new MInteger();
        MInteger length = new MInteger();

        List<BigDecimal> closePrices = klines.stream().map(Kline::getClose).collect(Collectors.toList());
        for (int i = 0; i < closePrices.size(); i++) {
            closePrice[i] = closePrices.get(i).doubleValue();
        }

        Core c = new Core();
        RetCode retCode = c.rsi(0, closePrices.size() - 1, closePrice, 14, begin, length, out);
        log.info(String.valueOf(out[closePrices.size() - 1]));
    }


    /**
     * 平仓清理
     */
//    @ScheduledByZyf(cron = "*/1 * * * * ?")
    private void closeHandle() {
        try {
//            List<Position> positions = RepeatUtil.repeat(symbol, tradeE::getPositions, 5);
//            if (positions.size() != 0) {
//                return;
//            }
            // 撤销计划订单
            List<Order> tpoList = tradeE.getTriggerPendingOrders(symbol);
            List<String> ids = tpoList.stream().map(Order::getOrderId).collect(Collectors.toList());
            if (ids.size() == 0) {
                return;
            }
            Boolean b = tradeE.triggerCancelOrders(symbol, ids);
            if (!b) {
                log.info("撤销计划订单，order：{}", tpoList);
            }
            log.info("平仓清理，撤销所有计划订单");
        } catch (Exception e) {
            e.printStackTrace();
            log.error(e.getMessage());
        }

    }

    /**
     * 撤销所有订单
     */
    private void cancelAll() {
        // 撤销委托单
        List<Order> poList = tradeE.getPendingOrders(symbol);
        log.info(poList.toString());
        List<String> ids = poList.stream().map(Order::getOrderId).collect(Collectors.toList());
        if (ids.size() == 0) {
            return;
        }
        Boolean b = tradeE.cancelOrders(symbol, ids);
        if (!b) {
            log.info("撤销委托单失败，order：{}", poList);
        }

        // 撤销计划订单
        List<Order> tpoList = tradeE.getTriggerPendingOrders(symbol);
        ids = tpoList.stream().map(Order::getOrderId).collect(Collectors.toList());
        if (ids.size() == 0) {
            return;
        }
        tradeE.triggerCancelOrders(symbol, ids);
        if (!b) {
            log.info("撤销计划订单，order：{}", tpoList);
        }
    }

    /**
     * 通过成交明细计算成交价格
     *
     * @return
     */
    private BigDecimal getDealPrice(String orderId) {
        BigDecimal price = BigDecimal.ZERO;
        List<Position> positions = tradeE.getPositions(symbol);
        if (positions.size() == 0) {
            throw new RuntimeException("持仓为0");
        }
        if (positions.size() > 1) {
            throw new RuntimeException("持仓 > 1");
        }

        for (Position p : positions) {
            price = price.add(p.getAvgCost());
        }

        log.info("" + positions.size());
        log.info(BigDecimal.valueOf(positions.size()).toString());
        price = MathUtil.adjustMinUtil(price.divide(BigDecimal.valueOf(positions.size()), RoundingMode.DOWN), minUtil, RoundingMode.DOWN);
        return price;
    }

    /**
     * 发邮件
     *
     * @param title   标题
     * @param content 内容
     */
    private void sendEmail(String title, String content) {
        StringBuilder emailTitle = new StringBuilder();
        emailTitle.append("okex合约策略rsi：").append(title);
        StringBuilder emailContent = new StringBuilder();
        emailContent.append("交易所：").append(exchangeName);
        emailContent.append("币对：").append(symbol);
        EmailServiceImpl.sendMail(host, user, password, fromMail, toMail, title, content);
    }

    /**
     * 是否有持仓
     */
    private boolean bPosition() {
        List<Position> positions = tradeE.getPositions(symbol);
        if (positions.size() != 0) {
            log.warn("条件成立，且有持仓");
            lowRsiState.setState(RsiState.State.PREPARE);
            highRsiState.setState(RsiState.State.PREPARE);
            return true;
        }
        return false;
    }

    public static void main(String[] args) {

    }
}
