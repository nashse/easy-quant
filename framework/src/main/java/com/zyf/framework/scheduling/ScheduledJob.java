package com.zyf.framework.scheduling;

import org.quartz.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 定时任务job
 * <br>不会同时执行多个任务
 * @author yuanfeng.z
 * @date 2020/7/25 1:33
 */
@DisallowConcurrentExecution
public class ScheduledJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        JobDetail jobDetail = jobExecutionContext.getJobDetail();
        JobDataMap jobDataMap = jobDetail.getJobDataMap();
        Method method = (Method)jobDataMap.get(ScheduledTask.METHOD);
        Object strategyIns = jobDataMap.get(ScheduledTask.STRATEGYINS);

        try {
            method.invoke(strategyIns);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InvocationTargetException e) {
            throw new RuntimeException(e.getMessage());
        }
    }
}
