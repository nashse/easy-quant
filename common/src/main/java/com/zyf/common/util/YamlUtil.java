package com.zyf.common.util;

import com.esotericsoftware.yamlbeans.YamlConfig;
import com.esotericsoftware.yamlbeans.YamlException;
import com.esotericsoftware.yamlbeans.YamlReader;
import com.esotericsoftware.yamlbeans.YamlWriter;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.reader.UnicodeReader;

import java.io.*;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * YML文件工具类
 *
 * @author yuanfeng.z
 * @date 2019-07-12
 */
public class YamlUtil {

    /**
     * 获取YAML接口
     *
     * @return
     */
    private static Yaml getYaml() {
        DumperOptions options = new DumperOptions();
        // 设置读取方式
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        return new Yaml(options);
    }

    /**
     * 读取Yaml文件
     *
     * @param src YAML文件
     * @return
     * @throws IOException
     */
    public static Map<String, Object> read(File src) throws IOException {
        Map<String, Object> result = new LinkedHashMap<>();
        FileInputStream inputStream = null;
        UnicodeReader reader = null;
        try {
            inputStream = new FileInputStream(src);
            reader = new UnicodeReader(inputStream);
            LinkedHashMap<String, Object> dataMap = getYaml().load(reader);
            if (dataMap != null) {
                return dataMap;
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return result;
    }

    /**
     * 写入Yaml文件
     *
     * @param dest  YAML文件
     * @param key   键
     * @param value 值
     * @throws IOException
     */
    public static void write(File dest, String key, Object value) throws IOException {
        FileInputStream inputStream = null;
        UnicodeReader reader = null;
        try {
            inputStream = new FileInputStream(dest);
            reader = new UnicodeReader(inputStream);
            Yaml yaml = getYaml();
            LinkedHashMap<String, Object> dataMap = yaml.load(reader);
            if (null == dataMap) {
                dataMap = new LinkedHashMap<>();
            }
            dataMap.put(key, value);
            yaml.dump(dataMap, new FileWriter(dest));
        } finally {
            if (reader != null) {
                reader.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    /**
     * 反序列化Bean
     *
     * @param file  YAML文件
     * @param clazz 类型
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T yamlToBean(File file, Class<T> clazz) throws FileNotFoundException, YamlException {
        YamlReader reader = new YamlReader(new FileReader(file));
        return reader.read(clazz);
    }

    /**
     * 反序列化Bean
     *
     * @param file   YAML文件
     * @param clazz  类型
     * @param config 反序列化配置
     * @param <T>
     * @return
     * @throws Exception
     */
    public static <T> T yamlToBean(File file, Class<T> clazz, YamlConfig config) throws FileNotFoundException, YamlException {
        YamlReader reader = new YamlReader(new FileReader(file), config);
        return reader.read(clazz);
    }


    /**
     * 序列化Bean
     *
     * @param file YAML文件
     * @param bean 序列化对象
     * @throws Exception
     */
    public static void beanToYaml(File file, Object bean) throws IOException {
        YamlWriter writer = new YamlWriter(new FileWriter(file));
        writer.write(bean);
        writer.close();
    }

    /**
     * 序列化Bean
     *
     * @param file   YAML文件
     * @param bean   序列化对象
     * @param config 序列化配置
     * @throws Exception
     */
    public static void beanToYaml(File file, Object bean, YamlConfig config) throws IOException {
        YamlWriter writer = new YamlWriter(new FileWriter(file), config);
        writer.write(bean);
        writer.close();
    }

}
