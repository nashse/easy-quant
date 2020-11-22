package com.zyf.strategy.rsi;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 定时器
 * @author yuanfeng.z
 * @date 2020/8/26 8:18
 */
public class Timer {

    private ScheduledExecutorService exec;

    /**
     * 倒数计数器（分钟）
     */
    private int count = 0;

    public void start(int c) {
        this.setCount(c);
        exec = Executors.newScheduledThreadPool(1);
        exec.scheduleAtFixedRate(new Task(),1,1, TimeUnit.MINUTES);
    }

    private class Task implements Runnable{
        @Override
        public void run() {
            count--;
            if (count <= 0) {
                count = 0;
                exec.shutdownNow();
            }
        }
    }

    public void setCount(int c) {
        count = c;
    }

    public int getCount() {
        return count;
    }

    /**
     * 是否倒数结束
     * @return
     */
    public boolean bEnd() {
        if (count == 0) {
            return true;
        }
        return  false;
    }
}
