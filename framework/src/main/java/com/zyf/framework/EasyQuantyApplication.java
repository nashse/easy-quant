package com.zyf.framework;

import com.zyf.framework.boot.ApplicationContext;
import com.zyf.framework.boot.BaseStrategy;
import com.zyf.framework.boot.Environment;
import com.zyf.framework.scheduling.ScheduleTaskManager;
import com.zyf.framework.util.StopWatch;
import lombok.extern.slf4j.Slf4j;
import org.quartz.SchedulerException;

import java.lang.management.ManagementFactory;
import java.util.Map;

@Slf4j
public class EasyQuantyApplication {

    private Class<?> strategyClass;

    ApplicationContext ac = null;

    public EasyQuantyApplication(Class<?> strategySources) {
        this.strategyClass = strategySources;
    }

    public static void run(Class<?> strategySources, String... args) {
        new EasyQuantyApplication(strategySources).run(args);
    }

    /**
     * 运行入口
     * @param args 启动参数
     */
    public void run(String... args) {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        this.setStrategyClass(this.strategyClass);
        ac = new ApplicationContext(this.strategyClass, args);
        try {
            ac.run();
        } catch (IllegalAccessException e) {
            log.error(e.toString());
            throw new RuntimeException(e.getMessage());
        } catch (InstantiationException e) {
            log.error(e.toString());
            throw new RuntimeException(e.getMessage());
        } catch (NoSuchFieldException e) {
            log.error(e.toString());
            throw new RuntimeException(e.getMessage());
        } catch (Exception e) {
            log.error(e.toString());
            throw new RuntimeException(e.getMessage());
        }

        this.shutdownHook();
        stopWatch.stop();

        log.info(getStartedMessage(stopWatch).toString());

    }

    public void setStrategyClass(Class<?> strategySources) {
        this.strategyClass = strategySources;
    }

    private String getApplicationName() {
        return (this.strategyClass != null ? this.strategyClass.getName()
                : "application");
    }

    private StringBuilder getStartedMessage(StopWatch stopWatch) {
        StringBuilder message = new StringBuilder();
        message.append("Started ");
        message.append(getApplicationName());
        message.append(" in ");
        message.append(stopWatch.getTotalTimeSeconds());
        try {
            double uptime = ManagementFactory.getRuntimeMXBean().getUptime() / 1000.0;
            message.append(" seconds (JVM running for " + uptime + ")");
        }
        catch (Throwable ex) {
            // No JVM time available
        }
        return message;
    }

    /**
     * 程序退出清理
     */
    public void shutdownHook() {
        for (Map.Entry<String, Object> pair : ac.getBeanDefinitionMap().entrySet()) {
            BaseStrategy baseStrategy = (BaseStrategy)pair.getValue();
        }
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
            @Override
            public void run()
            {
                log.info("Execute Hook.....");
                try {
                    ScheduleTaskManager.getInstance().shutdown();

                    for (Map.Entry<String, Object> pair : ac.getBeanDefinitionMap().entrySet()) {
                        BaseStrategy baseStrategy = (BaseStrategy)pair.getValue();
                        baseStrategy.destroy();
                    }
                } catch (SchedulerException e) {
                    e.printStackTrace();
                }
                log.info("Execute Hook Finish");
            }
        }));
    }

    public ApplicationContext getApplicationContext() {
        return ac;
    }
}
