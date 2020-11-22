package com.zyf.common.util;

import com.zyf.common.model.Kline;
import org.ta4j.core.Bar;
import org.ta4j.core.BarSeries;
import org.ta4j.core.BaseBar;
import org.ta4j.core.BaseBarSeries;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.List;

/**
 * 指标工具类
 *
 * @author yuanfeng.z
 * @date 2020/8/17 7:24
 */
public class IndexUtil {

    /**
     * 其他k线级别
     * @param lines k线
     * @param granularity k线粒度
     * @return
     */
    public static BarSeries toBarSeries(List<Kline> lines, int granularity) {
        BarSeries series = new BaseBarSeries();
        if ((lines != null) && !lines.isEmpty()) {
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
                // reverse the list to feed
                // the List<Bar> correctly.
                Collections.reverse(lines);
            }
            // build the list of populated bars
            buildSeries(series, beginTime, endTime, granularity, lines);

        }
        return series;
    }

    /**
     * lines与granularity级别相同
     * @param lines k线
     * @param granularity k线粒度
     * @return
     */
    public static BarSeries toBarSeriesSame(List<Kline> lines, String granularity) {
        BarSeries series = new BaseBarSeries();
        if ((lines != null) && !lines.isEmpty()) {
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
                // reverse the list to feed
                // the List<Bar> correctly.
                Collections.reverse(lines);
            }
            // build the list of populated bars
            buildSeriesSame(series, beginTime, endTime, Integer.valueOf(granularity), lines);
        }
        return series;
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
    private static void buildSeriesSame(BarSeries series, ZonedDateTime beginTime, ZonedDateTime endTime, int duration,
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
}
