package com.zyf.strategy;

import com.zyf.framework.EasyQuantyApplication;

/**
 * 策略主入口
 *
 * @author yuanfeng.z
 * @date 2020/7/27 19:15
 */
public class Main {
    public static void main(String[] args) {
        String[] strArg = {"--config-name:config.json"};
        EasyQuantyApplication.run(Strategy.class, strArg);
    }
}
