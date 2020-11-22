package com.zyf.strategy.triplefilter;

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
import org.ta4j.core.indicators.MACDIndicator;
import org.ta4j.core.indicators.RSIIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static java.math.BigDecimal.ONE;
import static java.math.BigDecimal.ZERO;

/**
 * 三重滤网交易系统
 *
 * @author yuanfeng.z
 * @date 2020/9/8 16:46
 */
@Slf4j
public class TripleFilterTradingStrategy extends BaseStrategy {

    /**
     * dpo下界
     */
    private Double lowLimit = -50d;
    /**
     * dpo上界
     */
    private Double highLimit = 50d;

    /**
     * dpo低界限状态
     */
    private RsiState lowDpoState = new RsiState();

    /**
     * dpo高界限状态
     */
    private RsiState highDpoState = new RsiState();

    /**
     * rsi下界
     */
    private Double rsiLowLimit = 40d;

    /**
     * rsi中界
     */
    private Double rsiMidLimit = 50d;

    /**
     * rsi上界
     */
    private Double raiHighLimit = 60d;

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
    private int count = 1;

    /**
     * 是否运行
     */
    private Boolean bRun = false;

    /**
     * kline级别 60：1min 300：5min
     */
    private String granularityMax = "1800";

    /**
     * kline级别 60：1min 300：5min
     */
    private String granularityMid = "300";

    /**
     * kline级别 60：1min 300：5min
     */
    private String granularityMin = "60";


    /**
     * 三层滤网信号
     */
    private SingleState singleStateMax = SingleState.NOT_SINGLE;

    /**
     * 二层滤网信号
     */
    private SingleState singleStateMid = SingleState.NOT_SINGLE;


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
        this.macdSignalMax();
        this.rsiSignalMid();
        this.thirdFilterScreen();
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
     * dpo指标过滤器
     * 大周期
     *
     * @return
     */
    private void macdSignalMax() {
        List<Kline> klines = mdE.getKline(symbol, granularityMax);
//        klines = klines.subList(0, 30);
        BarSeries barSeries = IndexUtil.toBarSeriesSame(klines, granularityMax);

        MACDIndicator macdIndicator = new MACDIndicator(new ClosePriceIndicator(barSeries));
        Num preNum = macdIndicator.getValue(macdIndicator.getBarSeries().getEndIndex() - 1);
        Num num = macdIndicator.getValue(macdIndicator.getBarSeries().getEndIndex());

        if (preNum.doubleValue() > 0 &&
                num.doubleValue() > 0) {
            if (preNum.doubleValue() < num.doubleValue()) {
//                log.info("第一层滤网做多");
//                log.info("preNum : " + preNum);
//                log.info("num : " + num);
                singleStateMax = SingleState.LONG;
                return;
            }
        }

        if (preNum.doubleValue() < 0 &&
                num.doubleValue() < 0) {
            if (preNum.doubleValue() > num.doubleValue()) {
//                log.info("第一层滤网做多");
//                log.info("preNum : " + preNum);
//                log.info("num : " + num);
                singleStateMax = SingleState.LONG;
                return;
            }
        }

        if (preNum.doubleValue() < 0 &&
                num.doubleValue() < 0) {
            if (preNum.doubleValue() < num.doubleValue()) {
//                log.info("第一层滤网做空");
//                log.info("preNum : " + preNum);
//                log.info("num : " + num);
                singleStateMax = SingleState.SHORT;
                return;
            }
        }

        if (preNum.doubleValue() > 0 &&
                num.doubleValue() > 0) {
            if (preNum.doubleValue() > num.doubleValue()) {
//                log.info("第一层滤网做空");
//                log.info("preNum : " + preNum);
//                log.info("num : " + num);
                singleStateMax = SingleState.SHORT;
                return;
            }
        }

        singleStateMax = SingleState.NOT_SINGLE;
    }

    /**
     * rsi信号
     */
    private void rsiSignalMid() {
        if (SingleState.NOT_SINGLE.equals(singleStateMax)) {
//            reset();
            return;
        }
        if (SingleState.NOT_SINGLE.compareTo(singleStateMid) != 0){
            return;
        }
        // 天数
        final int N = 14;
        List<Kline> klines = mdE.getKline(symbol, granularityMid);
        Collections.reverse(klines);
        BarSeries barSeries = IndexUtil.toBarSeriesSame(klines, granularityMid);

        RSIIndicator rsiIndicator = new RSIIndicator(new ClosePriceIndicator(barSeries), N);
        Num preNum = rsiIndicator.getValue(rsiIndicator.getBarSeries().getEndIndex() - 1);
        Num num = rsiIndicator.getValue(rsiIndicator.getBarSeries().getEndIndex());

        if (SingleState.LONG.equals(singleStateMax) &&
                (preNum.doubleValue() > rsiLowLimit &&
                        num.doubleValue() < rsiLowLimit)) {
//            log.info("第二层滤网做多");
//            log.info("rsi指标值preNum : " + preNum);
//            log.info("rsi指标值num : " + num);
            singleStateMid = SingleState.LONG;
            return;
        }

        if (SingleState.SHORT.equals(singleStateMax) &&
                (preNum.doubleValue() < raiHighLimit &&
                        num.doubleValue() > raiHighLimit)) {
//            log.info("第二层滤网做空");
//            log.info("rsi指标值preNum : " + preNum);
//            log.info("rsi指标值num : " + num);
            singleStateMid = SingleState.SHORT;
            return;
        }

        singleStateMid = SingleState.NOT_SINGLE;
    }

    private BigDecimal preClosePrice = ZERO;
    /**
     * 第三层滤网
     */
    private void thirdFilterScreen() {
        if (SingleState.NOT_SINGLE.equals(singleStateMid)) {
            return;
        }

        // 天数
        final int N = 14;
        List<Kline> klines = mdE.getKline(symbol, granularityMid);
        BarSeries barSeries = IndexUtil.toBarSeriesSame(klines, granularityMid);

        RSIIndicator rsiIndicator = new RSIIndicator(new ClosePriceIndicator(barSeries), N);
        Num preNum = rsiIndicator.getValue(rsiIndicator.getBarSeries().getEndIndex() - 1);
        Num num = rsiIndicator.getValue(rsiIndicator.getBarSeries().getEndIndex());

        // 说明挂单已成交
        if (bLongPosition()) {
            // 止损
            if (!bTriggerOrder()) {
                log.info("设置多单止损");
                BigDecimal prePreLowPrice = klines.get(klines.size() - 3).getLow();
                BigDecimal preLowPrice = klines.get(klines.size() - 2).getLow();
                Double stopPrice = Math.min(preLowPrice.doubleValue(), prePreLowPrice.doubleValue());
                BigDecimal lossPrice = MathUtil.adjustMinUtil(new BigDecimal(stopPrice.toString()), minUtil, RoundingMode.DOWN);
                cancelPendingOrdersAll();
                cancelTriggerPendingAll();
                log.info("prePreLowPrice : " + prePreLowPrice);
                log.info("preLowPrice : " + preLowPrice);
                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseLong", "-1", 5,
                        symbol, lossPrice, quantity, -1);
            }

            // 止盈
            stopProfit();
            return;
        } else if (bShortPosition()) {
            // 止损
            if (!bTriggerOrder()) {
                log.info("设置空单止损");
                BigDecimal prePreHighPrice = klines.get(klines.size() - 3).getHigh();
                BigDecimal preHighPrice = klines.get(klines.size() - 2).getHigh();
                Double stopPrice = Math.max(prePreHighPrice.doubleValue(), preHighPrice.doubleValue());
                BigDecimal lossPrice = MathUtil.adjustMinUtil(new BigDecimal(stopPrice.toString()), minUtil, RoundingMode.UP);
                cancelPendingOrdersAll();
                cancelTriggerPendingAll();
                log.info("prePreHighPrice : " + prePreHighPrice);
                log.info("preHighPrice : " + preHighPrice);
                id = CommomUtil.repeatOrderTrigger(tradeE, "triggerCloseShort", "-1", 5,
                        symbol, lossPrice, quantity, -1);
            }

            // 止盈
            stopProfit();
            return;
        }

        // 判断行情是否完成一个bar
        BigDecimal prePrice = klines.get(klines.size() - 2).getClose();
        if (preClosePrice.compareTo(prePrice) == 0) {
            return;
        }
        preClosePrice = prePrice;

        if (SingleState.LONG.equals(singleStateMid) &&
                (preNum.doubleValue() < rsiLowLimit && num.doubleValue() > rsiLowLimit)) {
            cancelPendingOrdersAll();
            cancelTriggerPendingAll();
            log.info("preNum : " + preNum);
            log.info("num : " + num);
            id = CommomUtil.repeatOrderMarket(tradeE, "openLongMarket", "-1", 5,
                    symbol, quantity, -1);
        }

        if (SingleState.SHORT.equals(singleStateMid) &&
                (preNum.doubleValue() > raiHighLimit && num.doubleValue() < raiHighLimit)) {
            cancelPendingOrdersAll();
            cancelTriggerPendingAll();
            log.info("preNum : " + preNum);
            log.info("num : " + num);
            id = CommomUtil.repeatOrderMarket(tradeE, "openShortMarket", "-1", 5,
                    symbol, quantity, -1);
        }
    }

    /**
     * 重置
     */
    private void reset() {
        singleStateMax = SingleState.NOT_SINGLE;
        singleStateMid = SingleState.NOT_SINGLE;
    }

    /**
     * 止盈
     */
    private void stopProfit() {
        List<Kline> klines = mdE.getKline(symbol, granularityMax);
        BarSeries barSeries = IndexUtil.toBarSeriesSame(klines, granularityMax);

        MACDIndicator macdIndicator = new MACDIndicator(new ClosePriceIndicator(barSeries));
        Num preNum = macdIndicator.getValue(macdIndicator.getBarSeries().getEndIndex() - 1);
        Num num = macdIndicator.getValue(macdIndicator.getBarSeries().getEndIndex());

        // 平多
        if ((preNum.doubleValue() > 0 &&
                num.doubleValue() > 0) &&
                SingleState.LONG.compareTo(singleStateMid) == 0) {
            if (preNum.doubleValue() > num.doubleValue()) {
                log.info("平多");
                log.info("preNum : " + preNum);
                log.info("num : " + num);
                id = CommomUtil.repeatOrderMarket(tradeE, "closeLongMarket", "-1", 5,
                        symbol, quantity, -1);
                reset();
                return;
            }
        }

        // 平空
        if ((preNum.doubleValue() > 0 &&
                num.doubleValue() > 0) &&
                SingleState.SHORT.compareTo(singleStateMid) == 0) {
            if (preNum.doubleValue() < num.doubleValue()) {
                log.info("平空");
                log.info("preNum : " + preNum);
                log.info("num : " + num);
                id = CommomUtil.repeatOrderMarket(tradeE, "closeShortMarket", "-1", 5,
                        symbol, quantity, -1);
                reset();
                return;
            }
        }

        // 平空
        if ((preNum.doubleValue() < 0 &&
                num.doubleValue() < 0) &&
                SingleState.LONG.compareTo(singleStateMid) == 0) {
            if (preNum.doubleValue() < num.doubleValue()) {
                log.info("平空");
                log.info("preNum : " + preNum);
                log.info("num : " + num);
                id = CommomUtil.repeatOrderMarket(tradeE, "closeShortMarket", "-1", 5,
                        symbol, quantity, -1);
                reset();
                return;
            }
        }

        // 平多
        if ((preNum.doubleValue() < 0 &&
                num.doubleValue() < 0) &&
                SingleState.SHORT.compareTo(singleStateMid) == 0) {
            if (preNum.doubleValue() > num.doubleValue()) {
                log.info("平多");
                log.info("preNum : " + preNum);
                log.info("num : " + num);
                id = CommomUtil.repeatOrderMarket(tradeE, "closeLongMarket", "-1", 5,
                        symbol, quantity, -1);
                reset();
                return;
            }
        }
    }

    /**
     * 是否有止盈止损单
     */
    private Boolean bTriggerOrder() {
        List<Order> tpoList = tradeE.getTriggerPendingOrders(symbol);
        if (tpoList.size() > 0) {
            return true;
        }
        return false;
    }

    /**
     * 撤销所有挂单
     */
    private void cancelPendingOrdersAll() {
        // 撤销委托单
        List<Order> poList = tradeE.getPendingOrders(symbol);
        List<String> ids = poList.stream().map(Order::getOrderId).collect(Collectors.toList());
        if (ids.size() == 0) {
            return;
        }
        Boolean b = tradeE.cancelOrders(symbol, ids);
        if (!b) {
            log.info("撤销委托单失败，order：{}", poList);
        }
    }

    /**
     * 撤销计划订单
     */
    private void cancelTriggerPendingAll() {
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
            lowDpoState.setState(RsiState.State.PREPARE);
            highDpoState.setState(RsiState.State.PREPARE);
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
                lowDpoState.setState(RsiState.State.PREPARE);
                highDpoState.setState(RsiState.State.PREPARE);
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
                lowDpoState.setState(RsiState.State.PREPARE);
                highDpoState.setState(RsiState.State.PREPARE);
                return true;
            }
        }
        return false;
    }
}
