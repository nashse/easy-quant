package com.zyf.marketdata.http.future;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.zyf.baseservice.IMdExchange;
import com.zyf.baseservice.util.okex.OkexUtil;
import com.zyf.common.model.Depth;
import com.zyf.common.model.Kline;
import com.zyf.common.model.Precision;
import com.zyf.common.model.Ticker;
import com.zyf.common.model.enums.ExchangeEnum;
import com.zyf.common.okhttp.OkHttpV3ClientProxy;
import lombok.extern.slf4j.Slf4j;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;
import org.ta4j.core.indicators.DPOIndicator;
import org.ta4j.core.indicators.SMAIndicator;
import org.ta4j.core.indicators.helpers.ClosePriceIndicator;
import org.ta4j.core.num.Num;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * okex期货行情类
 *
 * @author yuanfeng.z
 * @date q 20:54
 */
@Slf4j
public class OkexMdFutExchange implements IMdExchange {


    /**
     * 协议
     */
    private static final String PROTOCOL = "https://";

    /**
     * 网站
     */
    private static final String SITE = "www.okex.com";

    /**
     * url
     */
    private static final String URL = PROTOCOL + SITE;

    /**
     * get请求方法
     */
    private static final String METHOD_GET = "GET";

    /**
     * 交易所名字
     */
    private final ExchangeEnum name = ExchangeEnum.OKEXV3;

    /**
     * depth资源路径
     */
    private static final String DEPTH = "/api/swap/v3/instruments/<instrument_id>/depth";

    /**
     * Kline资源路径
     */
    private static final String KLINE = "/api/swap/v3/instruments/<instrument_id>/candles";


    /**
     * 单例模式
     *
     * @return
     */
    public static OkexMdFutExchange okexMdFutExchange = new OkexMdFutExchange();

    public static OkexMdFutExchange getInstance() {
        return okexMdFutExchange;
    }

    @Override
    public Ticker getTicker(String symbol) {
        return null;
    }

    @Override
    public List<Kline> getKline(String symbol, String granularity) {
        /*获取Kline数据*/
        StringBuilder sb = new StringBuilder();
        String kline = KLINE.replace("<instrument_id>", symbol);
        // 15min
        sb.append(URL).append(kline).append("?").append("granularity=").append(granularity);
        String klineStr = OkHttpV3ClientProxy.get(sb.toString());
        /*适配*/
        JSONArray array = JSONArray.parseArray(klineStr);
        return OkexUtil.parseKlines(array);
    }

    public static void main(String[] args) {
        final String granularity = "900";
        OkexMdFutExchange okexMdFutExchange = new OkexMdFutExchange();
        List<Kline> lines = okexMdFutExchange.getKline("MNBTC-USDT-SWAP", granularity);

        final int max = 21;
        lines = lines.subList(2, max + 2);
//        final int max = 21;
//        lines = lines.subList(1, max + 1);


//        lines = lines.subList(1, max+1);

        BarSeries series = new BaseBarSeries();
        if ((lines != null) && !lines.isEmpty()) {
            log.info(lines.get(0).toString());
            log.info(lines.get(lines.size() - 1).toString());
            // Getting the first and last trades timestamps
            ZonedDateTime beginTime = ZonedDateTime
                    .ofInstant(Instant.ofEpochMilli(lines.get(0).getTimestamp()), ZoneId.systemDefault());
            ZonedDateTime endTime = ZonedDateTime.ofInstant(
                    Instant.ofEpochMilli(lines.get(lines.size() - 1).getTimestamp()),
                    ZoneId.systemDefault());
            if (beginTime.isAfter(endTime)) {
                Instant beginInstant = beginTime.toInstant();
                Instant endInstant = endTime.toInstant();
                beginTime = ZonedDateTime.ofInstant(endInstant, ZoneId.systemDefault());
                endTime = ZonedDateTime.ofInstant(beginInstant, ZoneId.systemDefault());
                // Since the CSV file has the most recent trades at the top of the file, we'll
                // reverse the list to feed
                // the List<Bar> correctly.
                Collections.reverse(lines);
            }
            // build the list of populated bars
            buildSeries2(series, beginTime, endTime, 900, lines);
        }

        BigDecimal close = lines.get(0).getClose();
        System.out.println("close: " + close);

        int realBarCount;
        BigDecimal barCount = BigDecimal.ZERO;
        int i = 0;
        for (realBarCount = 0; realBarCount < max - (max / 2 + 1); ++realBarCount) {
            barCount = lines.get(realBarCount).getClose().add(barCount);
            i++;
        }
        realBarCount = i;
        BigDecimal x = barCount.divide(BigDecimal.valueOf(realBarCount), 1);


        BigDecimal df = close.subtract(x);
        System.out.println("First close price: " + df);

//        BarSeries series = new BaseBarSeries();
        DPOIndicator dpoIndicator = new DPOIndicator(series, 21);
        System.out.println("First close price: " + dpoIndicator.getValue(21 - 1));

        SMAIndicator smaIndicator = new SMAIndicator(new ClosePriceIndicator(series), max);
//        System.out.println("First close price: " + smaIndicator.getValue(max - 1));

    }

    /**
     * Builds a list of populated bars from csv data.
     *
     * @param beginTime the begin time of the whole period
     * @param endTime   the end time of the whole period
     * @param duration  the bar duration (in seconds)
     * @param lines     the csv data returned by CSVReader.readAll()
     */
    @SuppressWarnings("deprecation")
    private static void buildSeries(BarSeries series, ZonedDateTime beginTime, ZonedDateTime endTime, int duration,
                                    List<Kline> lines) {

        Duration barDuration = Duration.ofSeconds(duration);
        ZonedDateTime barEndTime = beginTime;
        // line number of trade data
        int i = 0;
        do {
            // build a bar
            barEndTime = barEndTime.plus(barDuration);
            Bar bar = new BaseBar(barDuration, barEndTime, series.function());
            do {
                // get a trade
                Kline tradeLine = lines.get(i);

                ZonedDateTime tradeTimeStamp = ZonedDateTime
                        .ofInstant(Instant.ofEpochMilli(tradeLine.getTimestamp()), ZoneId.systemDefault());
                // if the trade happened during the bar
                if (bar.inPeriod(tradeTimeStamp)) {
                    // add the trade to the bar
                    double tradePrice = tradeLine.getClose().doubleValue();
                    double tradeVolume = tradeLine.getVolume().doubleValue();
                    bar.addTrade(tradeVolume, tradePrice, series.function());
                } else {
                    // the trade happened after the end of the bar
                    // go to the next bar but stay with the same trade (don't increment i)
                    // this break will drop us after the inner "while", skipping the increment
                    break;
                }
                i++;
            } while (i < lines.size());
            // if the bar has any trades add it to the bars list
            // this is where the break drops to
            if (bar.getTrades() > 0) {
                series.addBar(bar);
            }
        } while (barEndTime.isBefore(endTime));
    }

    /**
     * Builds a list of populated bars from csv data.
     *
     * @param beginTime the begin time of the whole period
     * @param endTime   the end time of the whole period
     * @param duration  the bar duration (in seconds)
     * @param lines     the csv data returned by CSVReader.readAll()
     */
    @SuppressWarnings("deprecation")
    private static void buildSeries2(BarSeries series, ZonedDateTime beginTime, ZonedDateTime endTime, int duration,
                                     List<Kline> lines) {
            Duration barDuration = Duration.ofSeconds(duration);
        int i = 0;
        do {
            // get a trade
            Kline tradeLine = lines.get(i);

            ZonedDateTime tradeTimeStamp = ZonedDateTime
                    .ofInstant(Instant.ofEpochMilli(tradeLine.getTimestamp()), ZoneId.systemDefault());
            Bar bar = new BaseBar(barDuration, tradeTimeStamp, series.function());
            double tradePrice = tradeLine.getClose().doubleValue();
            double tradeVolume = tradeLine.getVolume().doubleValue();
            bar.addTrade(tradeVolume, tradePrice, series.function());
            i++;
            if (bar.getTrades() > 0) {
                series.addBar(bar);
            }
        } while (i < lines.size());
    }

    @Override
    public Depth getDepth(String symbol) {
        /*获取depth数据*/
        StringBuilder sb = new StringBuilder();
        final int size = 10;
        String depth = DEPTH.replace("<instrument_id>", symbol);
        sb.append(URL).append(depth).append("?").append("size=").append(size);
        String depthStr = OkHttpV3ClientProxy.get(sb.toString());

        /*适配*/
        JSONObject jo = JSONObject.parseObject(depthStr);
        return OkexUtil.parseDepth(jo);
    }

    @Override
    public Map<String, Precision> getPrecisions() {
        return null;
    }
}
