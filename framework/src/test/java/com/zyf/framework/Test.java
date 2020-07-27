package com.zyf.framework;

public class Test {
    public static void main(String[] args) {
        String[] args1 = {"--config-name:config.json"};
        EasyQuantyApplication.run(com.zyf.framework.Strategy.class, args1);
    }
}
