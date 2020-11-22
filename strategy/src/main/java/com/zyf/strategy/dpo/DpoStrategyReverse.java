package com.zyf.strategy.dpo;

import com.alibaba.fastjson.JSONObject;
import com.zyf.common.model.Kline;
import com.zyf.common.model.Order;
import com.zyf.common.model.Position;
import com.zyf.common.model.enums.Side;
import com.zyf.common.util.CommomUtil;
import com.zyf.common.util.IndexUtil;
import com.zyf.common.util.MathUtil;
import com.zyf.framework.boot.BaseStrategy;
import com.zyf.framework.scheduling.interfaces.ScheduledByZyf;
import com.zyf.framework.service.impl.EmailServiceImpl;
import com.zyf.framework.util.FileUtil;
import com.zyf.strategy.rsi.RsiState;
import com.zyf.strategy.rsi.Timer;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.BarSeries;
import org.ta4j.core.indicators.DPOIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

/**
 * 反向dpo策略
 * 周期要低
 * @author yuanfeng.z
 * @date 2020/9/8 21:58
 */
@Slf4j
public class DpoStrategyReverse extends BaseStrategy {

    /**
     * rsi下界
     */
    private Double lowLimit = -50d;
    /**
     * rsi上界
     */
    private Double highLimit = 50d;
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
    private BigDecimal stopProfitRate = new BigDecimal("0.004");

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
     * 是否运行
     */
    private Boolean bRun = false;

    /**
     * kline级别 60：1min 300：5min
     */
    private  String granularity = "300";


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
        this.dpoSignal();
//        this.smaSignal();

        try {
            // 上限
            if (RsiState.State.FINISH.equals(highRsiState.getState())) {
                if (this.bPosition()) {
                    return;
                }

                if (!timer.bEnd()) {
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

                BigDecimal dealPrice = this.getDealPrice(id);
                basePrice = dealPrice;
                log.info("basePrice：{}", basePrice);
                BigDecimal lossPrice = MathUtil.adjustMinUtil(basePrice.multiply(ONE.add(stopLossRate)), minUtil, RoundingMode.DOWN);
                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseShort", "-1", 5,
                        symbol, lossPrice, quantity, -1);
                log.info("平空止损价：{}", lossPrice);

                basePrice = dealPrice;
                log.info("basePrice：{}", basePrice);
                BigDecimal profitPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.subtract(stopProfitRate)), minUtil, RoundingMode.DOWN);
                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseShort", "-1", 5,
                        symbol, profitPrice, quantity, -1);
                log.info("平空止盈价：{}", profitPrice);

//                BigDecimal dealPrice = this.getDealPrice(id);
//                log.info("跟踪止损价格：{}", dealPrice);
//                BigDecimal lossPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.add(stopLossRate)), minUtil, RoundingMode.UP);
//                CommomUtil.repeatOrderTracking(tradeE, "trackingCloseShort", "-1", 5,
//                        symbol, dealPrice, quantity, stopLossRate);

//                BigDecimal dealPrice = this.getDealPrice(id);
//                log.info("开仓价格：{}", dealPrice);
//                BigDecimal lossPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.add(stopLossRate)), minUtil, RoundingMode.UP);
//                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseShort", "-1", 5,
//                        symbol, lossPrice, quantity, -1);
//                log.info("止损价：{}", lossPrice);

//                BigDecimal profitPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.subtract(stopProfitRate)), minUtil, RoundingMode.DOWN);
//                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseShort", "-1", 5,
//                        symbol, profitPrice, quantity, -1);
//                log.info("止盈价：{}", profitPrice);

                highRsiState.setState(RsiState.State.PREPARE);
                timer.start(count);
            }

            if (RsiState.State.FINISH.equals(lowRsiState.getState())) {
                if (this.bPosition()) {
                    return;
                }

                if (!timer.bEnd()) {
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

                BigDecimal dealPrice = this.getDealPrice(id);
                basePrice = dealPrice;
                log.info("basePrice：{}", basePrice);
                BigDecimal lossPrice = MathUtil.adjustMinUtil(basePrice.multiply(ONE.subtract(stopLossRate)), minUtil, RoundingMode.UP);
                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseLong", "-1", 5,
                        symbol, lossPrice, quantity, -1);
                log.info("平多止损价：{}", lossPrice);

                basePrice = dealPrice;
                log.info("basePrice：{}", basePrice);
                BigDecimal profitPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.add(stopProfitRate)), minUtil, RoundingMode.UP);
                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseLong", "-1", 5,
                        symbol, profitPrice, quantity, -1);
                log.info("平多止盈价：{}", profitPrice);

//                BigDecimal dealPrice = this.getDealPrice(id);
//                log.info("跟踪止损价格：{}", dealPrice);
//                BigDecimal lossPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.subtract(stopLossRate)), minUtil, RoundingMode.UP);
//                CommomUtil.repeatOrderTracking(tradeE, "trackingCloseLong", "-1", 5,
//                        symbol, dealPrice, quantity, stopLossRate);

//                BigDecimal dealPrice = this.getDealPrice(id);
//                log.info("开仓价格：{}", dealPrice);
//                BigDecimal lossPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.subtract(stopLossRate)), minUtil, RoundingMode.UP);
//                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseLong", "-1", 5,
//                        symbol, lossPrice, quantity, -1);
//                log.info("止损价：{}", lossPrice);

//                BigDecimal profitPrice = MathUtil.adjustMinUtil(dealPrice.multiply(ONE.add(stopProfitRate)), minUtil, RoundingMode.UP);
//                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseLong", "-1", 5,
//                        symbol, profitPrice, quantity, -1);
//                log.info("止盈价：{}", profitPrice);

                lowRsiState.setState(RsiState.State.PREPARE);
                timer.start(count);
            }
        } catch (Exception e) {
            this.sendEmail("error", e.toString());
            e.printStackTrace();
            log.error(e.toString());
        }

    }

    /**
     * 0亏损标准
     */
    private boolean zeroLoss = false;

    /**
     * 跟踪止盈
     */
    @ScheduledByZyf(cron = "*/1 * * * * ?")
    public void trackingProfit() {
        if (zeroLoss) {
            return;
        }

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
                zeroLoss = true;
            }
        } else if (Side.OPEN_LONG.equals(side)) {
            if (bid1.compareTo(basePrice.multiply(ONE.add(stopProfitRate))) == 1) {
                basePrice = bid1;
                log.info("basePrice：{}", bid1);
                BigDecimal lossPrice = MathUtil.adjustMinUtil(bid1.multiply(ONE.subtract(stopProfitRate)), minUtil, RoundingMode.UP);
                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseLong", "-1", 5,
                        symbol, lossPrice, quantity, -1);
                log.info("平多止损价：{}", lossPrice);
                zeroLoss = true;
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
     * dpo指标过滤器,
     * 15min,
     * 过滤大涨跌行情，避免在这过程中开仓,
     *
     * @return true为震荡行情
     */
    private void dpoSignal() {
        final int max = 10;
        List<Kline> klines = mdE.getKline(symbol, granularity);
//        klines = klines.subList(0, max);
        BarSeries barSeries = IndexUtil.toBarSeriesSame(klines, granularity);

        DPOIndicator dpoIndicator = new DPOIndicator(barSeries, max);
        Num preNum = dpoIndicator.getValue( dpoIndicator.getBarSeries().getEndIndex() - 1);
        Num num = dpoIndicator.getValue(dpoIndicator.getBarSeries().getEndIndex());

        if (RsiState.State.PREPARE.equals(lowRsiState.getState()) &&
                ((preNum.doubleValue() > lowLimit) &&
                        (num.doubleValue() <= lowLimit))) {
            lowRsiState.setState(RsiState.State.FINISH);
            log.info("lowRsiState is finish, rsiValue");
            log.info("dpo前一个指标值 : " + preNum);
            log.info("dpo指标值 : " + num);
        }

        if (RsiState.State.PREPARE.equals(highRsiState.getState()) &&
                (preNum.doubleValue() < highLimit) &&
                (num.doubleValue() >= highLimit)) {
            highRsiState.setState(RsiState.State.FINISH);
            log.info("highRsiState is finish, rsiValue");
            log.info("dpo前一个指标值 : " + preNum);
            log.info("dpo指标值 : " + num);
        }
    }

    /**
     * ma指标过滤器,
     * 15min,
     * 过滤大涨跌行情，避免在这过程中开仓,
     *
     * @return true为震荡行情
     */
    private void smaSignal() {
        final int max = 60;
        List<Kline> klines = mdE.getKline(symbol, granularity);
//        klines = klines.subList(0, max);
        BarSeries barSeries = IndexUtil.toBarSeriesSame(klines, granularity);

        SMAIndicator smaIndicator = new SMAIndicator(new ClosePriceIndicator(barSeries), max);
        Num prepreNum = smaIndicator.getValue(smaIndicator.getBarSeries().getEndIndex() - 2);
        Num preNum = smaIndicator.getValue(smaIndicator.getBarSeries().getEndIndex() - 1);
//        log.info("sma指标值 : " + num);

        BigDecimal prepreClose = klines.get(klines.size() - 3).getClose();
        BigDecimal preClose = klines.get(klines.size() - 2).getClose();
//        log.info("sma prepreClose : " + prepreClose);
//        log.info("sma preClose : " + preClose);

        // 做空止盈
        if (prepreClose.compareTo(new BigDecimal(String.valueOf(prepreNum.doubleValue()))) == -1 &&
                preClose.compareTo(new BigDecimal(String.valueOf(preNum.doubleValue()))) == 1) {
            if (this.bShortPosition()) {
                log.info("做空止盈");
                log.info("prepreClose kline收盘价 : " + prepreClose);
                log.info("sma prepre指标值 : " + prepreNum);
                log.info("preClose kline收盘价 : " + preClose);
                log.info("sma pre指标值 : " + preNum);
                id = CommomUtil.repeatOrderMarket(tradeE, "closeShortMarket", "-1", 5,
                        symbol, quantity, -1);
                return;
            }
        }

        // 做多止盈
        if (prepreClose.compareTo(new BigDecimal(String.valueOf(prepreNum.doubleValue()))) == 1 &&
                preClose.compareTo(new BigDecimal(String.valueOf(preNum.doubleValue()))) == -1) {
            if (this.bLongPosition()) {
                log.info("做多止盈");
                log.info("prepreClose kline收盘价 : " + prepreClose);
                log.info("sma prepre指标值 : " + prepreNum);
                log.info("preClose kline收盘价 : " + preClose);
                log.info("sma pre指标值 : " + preNum);
                id = CommomUtil.repeatOrderMarket(tradeE, "closeLongMarket", "-1", 5,
                        symbol, quantity, -1);
                return;
            }

        }
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
        zeroLoss = false;
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

    /**
     * 是否有long持仓
     */
    private boolean bLongPosition() {
        List<Position> positions = tradeE.getPositions(symbol);
        if (positions.size() != 0) {
            if (Side.OPEN_LONG.getValue().equals(positions.get(0).getSide().getValue())) {
                lowRsiState.setState(RsiState.State.PREPARE);
                highRsiState.setState(RsiState.State.PREPARE);
                return true;
            }
        }
        return false;
    }

    /**
     * 是否有Short持仓
     */
    private boolean bShortPosition() {
        List<Position> positions = tradeE.getPositions(symbol);
        if (positions.size() != 0) {
            if (Side.OPEN_SHORT.getValue().equals(positions.get(0).getSide().getValue())) {
                lowRsiState.setState(RsiState.State.PREPARE);
                highRsiState.setState(RsiState.State.PREPARE);
                return true;
            }
        }
        return false;
    }
}
