package com.zyf.strategy;

import com.zyf.framework.EasyQuantyApplication;

/**
 * 策略主入口
 *
 * @author yuanfeng.z
 * @date 2020/7/27 19:15
 */
public class Main {
    public static void main(String[] args) throws ClassNotFoundException {
//        args[0] = "com.zyf.strategy.OkexGridStrategy";
//        args[1] = "--config-name:grid.json";
//        String[] argss = new String[2];
//        argss[0] = "com.zyf.strategy.OkexGridStrategy";
//        argss[1] = "--config-name:grid.json";
        EasyQuantyApplication.run(Class.forName(args[0]), args[1]);
    }
}
