package com.zyf.strategy;

import com.zyf.framework.boot.BaseStrategy;
import com.zyf.framework.scheduling.interfaces.ScheduledByZyf;
import com.zyf.marketdata.proxy.MdExchangeProxy;
import lombok.extern.slf4j.Slf4j;

/**
 * 策略类demo
 * @author yuanfeng.z
 * @date 2020/7/27 1:44
 */
@Slf4j
public class Strategy extends BaseStrategy {

    private String profit;

    private MdExchangeProxy huobiproE;

    @Override
    @ScheduledByZyf(cron = "0/1 * * * * ?")
    //@ScheduledByZyf(initialDelay = 20000, fixedDelay = 2000)
    protected void run() {
        log.info(this.toString());
        log.info(mdE.getDepth(this.symbol).toString());
        try {
            Thread.sleep(6000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
