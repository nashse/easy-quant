package com.zyf.framework;

import com.zyf.framework.boot.ApplicationContext;
import com.zyf.framework.boot.Environment;
import com.zyf.framework.util.StopWatch;
import lombok.extern.slf4j.Slf4j;

import java.lang.management.ManagementFactory;

@Slf4j
public class EasyQuantyApplication {

    private Class<?> strategyClass;

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
        ApplicationContext ac = new ApplicationContext(this.strategyClass, args);
        try {
            ac.run();
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e.getMessage());
        } catch (InstantiationException e) {
            throw new RuntimeException(e.getMessage());
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e.getMessage());
        }
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
}
