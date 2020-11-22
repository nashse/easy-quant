package com.zyf.framework.scheduling;

import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.lang.reflect.Method;
import java.util.Date;

/**
 * 定时任务类
 * <br>建造者模式
 * @author yuanfeng.z
 * @date 2020/7/25 1:10
 */
public class ScheduledTask {

    /**
     * quartz定时任务
     */
    private Scheduler scheduler;

    /**
     * 策略实例
     */
    private Object strategyIns;

    /**
     * 策略启动方法
     */
    private Method method;

    /**
     * 触发器
     */
    private Trigger trigger;

    /**
     * 定义Job的实例
     */
    private JobDetail jobDetail;

    public final static String METHOD = "method";

    public final static String STRATEGYINS = "strategyIns";

    /**
     * 创建定时任务
     * @param strategyIns 策略实例
     * @param method 策略启动方法
     * @return
     */
    public static ScheduledTask newScheduledTask(Object strategyIns, Method method) {
        return new ScheduledTask(strategyIns, method);
    }

    private ScheduledTask(Object strategyIns, Method method) {
        this.strategyIns = strategyIns;
        this.method = method;
    }

    /**
     * 创建quartz定时任务实例
     * @return
     */
    public ScheduledTask newScheduler() {
        try {
            this.scheduler = StdSchedulerFactory.getDefaultScheduler();
        } catch (SchedulerException e) {
            throw new RuntimeException(e.getMessage());
        }
        return this;
    }

    /**
     * 设置 JobDetail
     * @return
     */
    public ScheduledTask setJobDetail() {
        JobDataMap jobDataMap = new JobDataMap();
        jobDataMap.put(ScheduledTask.METHOD, this.method);
        jobDataMap.put(STRATEGYINS, this.strategyIns);
        this.jobDetail = JobBuilder.newJob(ScheduledJob.class).
                usingJobData(jobDataMap).
                build();
        return this;
    }

    /**
     * 设置延迟触发器
     * @param initialDelay 初始延迟时间
     * @param fixedDelay 每次轮询相隔延迟时间
     * @return
     */
    public ScheduledTask setFixedDelayTrigger(long initialDelay, long fixedDelay) {
            this.trigger = TriggerBuilder.newTrigger().
                    startAt(new Date(System.currentTimeMillis() + initialDelay)).
                    withSchedule(SimpleScheduleBuilder.
                            simpleSchedule().
                            withIntervalInMilliseconds(fixedDelay).
                            repeatForever()).
                    build();
        return this;
    }

    /**
     * 设置cron触发器
     * @param cron cron表达式
     * @return
     */
    public ScheduledTask setCronTrigger(String cron) {
            this.trigger = TriggerBuilder.newTrigger().
                    withSchedule(CronScheduleBuilder.cronSchedule(cron)).
                    build();
        return this;
    }

    /**
     * 建造者 build
     * @return
     */
    public ScheduledTask build() {

        try {
            this.scheduler.scheduleJob(this.jobDetail, this.trigger);
        } catch (SchedulerException e) {
            throw new RuntimeException(e.getMessage());
        }
        return this;
    }

    /**
     * 启动定时任务
     * @return
     */
    public ScheduledTask start() {
        try {
            this.scheduler.start();
        } catch (SchedulerException e) {
            throw new RuntimeException(e.getMessage());
        }
        return this;
    }

    public void shutdown() throws SchedulerException {
        this.scheduler.shutdown();
    }
}
