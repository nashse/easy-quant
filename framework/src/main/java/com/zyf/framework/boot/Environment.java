package com.zyf.framework.boot;

import cn.hutool.core.io.resource.ClassPathResource;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.JSONObject;
import com.zyf.framework.util.FileUtil;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.util.Objects;

/**
 * 环境配置类
 * @author yuanfeng.z
 * @date 2020/7/22 17:20
 */
@Slf4j
public class Environment {

    /**
     * 策略配置文件名前缀
     */
    private final static String CONFIG_PROPERTY = "--config-name:";

    /**
     * 策略配置默认
     */
    private final static String DEFAULT_CONFIG_NAME = "config.json";

    /**
     * 加载配置文件
     * @param args 程序入参
     * @return
     */
    public static JSONObject prepareConfig(String[] args) {
        String name = DEFAULT_CONFIG_NAME;

        // 若指定文件，则加载指定路径文件，否则使用默认路径
        for (String arg : args) {
            if (arg.startsWith(CONFIG_PROPERTY)) {
                name = arg.split(":")[1];
                log.info("config name is : {}", name);
                break;
            }
        }

        log.info("load default config");

        // 加载配置文件
        String property = FileUtil.readJsonFile(name);
        log.info(String.format("load file %s success.", name));

        JSONObject json;
        try {
            json = JSON.parseObject(property);
        } catch (JSONException e) {
            throw new JSONException("The config file JSON format is not correct");
        }
        if (Objects.isNull(json)) {
            throw new RuntimeException("The config file content is empty");
        }

        return json;
    }

}
