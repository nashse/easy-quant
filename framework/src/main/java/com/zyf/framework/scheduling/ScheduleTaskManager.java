package com.zyf.framework.scheduling;

import lombok.Getter;
import org.quartz.SchedulerException;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledExecutorService;

/**
 * 定时任务管理类
 *
 * @author yuanfeng.z
 * @date 2020/7/24 23:36
 */
@Getter
public class ScheduleTaskManager {

    /**
     * 策略任务列表
     */
    private List<ScheduledTask> scheduledTasks = new ArrayList<>(16);

    private static ScheduleTaskManager scheduleTaskManager = new ScheduleTaskManager();

    public static ScheduleTaskManager getInstance() {
        return scheduleTaskManager;
    }

    /**
     * 添加任务
     *
     * @param scheduledTask 定时任务
     */
    public void add(ScheduledTask scheduledTask) {
        this.scheduledTasks.add(scheduledTask);
    }

    /**
     * 启动任务
     */
    public void start() {
        for (ScheduledTask scheduledTask : this.scheduledTasks) {
            scheduledTask.start();
        }
    }

    public void shutdown() throws SchedulerException {
        for (ScheduledTask scheduledTask : this.scheduledTasks) {
            scheduledTask.shutdown();
        }
    }
}
