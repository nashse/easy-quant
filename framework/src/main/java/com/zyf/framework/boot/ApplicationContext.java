package com.zyf.framework.boot;

import cn.hutool.core.lang.Assert;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ser.Serializers;
import com.zyf.baseservice.IExchange;
import com.zyf.baseservice.IMdExchange;
import com.zyf.baseservice.ITradeExchange;
import com.zyf.framework.scheduling.ScheduleTaskManager;
import com.zyf.framework.scheduling.ScheduledTask;
import com.zyf.framework.scheduling.interfaces.*;
import com.zyf.marketdata.factory.MdExchangeFactory;
import com.zyf.marketdata.proxy.MdExchangeProxy;
import com.zyf.trade.factory.TradeExchangeFactory;
import com.zyf.trade.proxy.TradeExchangeProxy;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 应用内容类（核心类）
 * 加载策略配置文件
 * 实现类IOC功能（将配置对应值加载到对应类属性变量）
 * @author yuanfeng.z
 * @date 2020/7/22 18:37
 */
public class ApplicationContext {

    /**
     * 策略包名
     */
    private Class<?> strategySources;

    /**
     * 程序入参
     */
    private String[] args;

    /**
     * 配置参数
     * <br> json格式
     */
    private JSONObject propertyJson;

    /*******策略配置json字段名*********/

    private final static String CONFIGS_KEY = "configs";

    private final static String CONFIG_ATTRIBUTE = "config";

    private final static String CONFIG_SYMBOL_NAME = "symbol";

    private final static String CONFIG_STRATEGY_ID = "id";

    private final static String CONFIG_MD_EXCHANGE_NAME = "mdE";

    private final static String CONFIG_TRATE_EXCHANGE_NAME = "tradeE";

    private final static String CONFIG_EXCHANGE_NAME = "exchangeName";

    private final static String CONFIG_SECURITIESTYPE_NAME = "securitiesType";

    private final static String CONFIG_ACCESSKEY_NAME = "accessKey";

    private final static String CONFIG_SECRETKEY_NAME = "secretKey";

    /**
     * ioc容器
     * <strategyName, 策略实例>
     */
    private Map<String, Object> beanDefinitionMap = new ConcurrentHashMap<>();

    public ApplicationContext(Class<?> strategySources, String[] args) {
        this.args = args;
        this.strategySources = strategySources;
    }

    /**
     * 程序主入口方法
     * @throws IllegalAccessException
     * @throws NoSuchFieldException
     * @throws InstantiationException
     */
    public void run() throws IllegalAccessException, NoSuchFieldException, InstantiationException {
        // 预处理
        this.prepareRefresh();

        // 将配置信息加载到策略实例
        this.invokeProcessors();

        // 处理注解
        this.handleAnnotation();

        // 初始化策略
        this.prepareStrategy();

        // 启动定时任务
        this.startUpScheduler();
    }


    /**
     * 准备策略
     */
    private void prepareStrategy() {
        for (Map.Entry<String, Object> pair : this.beanDefinitionMap.entrySet()) {
            if (((BaseStrategy)pair.getValue()).bInit == 0) {
                initStrategy((BaseStrategy)pair.getValue());
            } else if (((BaseStrategy)pair.getValue()).bInit == 1) {
                recoveryStrategy((BaseStrategy)pair.getValue());
            }
        }
    }

    /**
     * 恢复策略
     */
    private void recoveryStrategy(BaseStrategy baseStrategy) {
        baseStrategy.recovery();
    }

    /**
     * 初始化策略
     */
    private void initStrategy(BaseStrategy baseStrategy) {
        baseStrategy.init();
    }

    /**
     * ioc前初始化（加载属性配置）
     */
    private void prepareRefresh() {
        this.propertyJson = Environment.prepareConfig(args);
    }

    /**
     * 反射处理
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws NoSuchFieldException
     */
    private void invokeProcessors() throws IllegalAccessException, InstantiationException, NoSuchFieldException {
        // 反射创建类(后期使用注解方式)
        JSONArray configs = this.propertyJson.getJSONArray(CONFIGS_KEY);
        if (Objects.isNull(configs)) {
            throw new RuntimeException("configs key: " + CONFIGS_KEY + " no exists");
        }

        for (int i = 0; i < configs.size(); i++) {
            JSONObject config = configs.getJSONObject(i);

            if (Objects.isNull(config.getString(CONFIG_MD_EXCHANGE_NAME))) {
                throw new RuntimeException("configs key: " + CONFIG_MD_EXCHANGE_NAME + " no exists");
            }
            if (Objects.isNull(config.getJSONObject(CONFIG_MD_EXCHANGE_NAME).getString(CONFIG_EXCHANGE_NAME))) {
                throw new RuntimeException("configs key: " + CONFIG_EXCHANGE_NAME + " no exists");
            }
            if (Objects.isNull(config.getString(CONFIG_SYMBOL_NAME))) {
                throw new RuntimeException("configs key: " + CONFIG_SYMBOL_NAME + " no exists");
            }
            if (Objects.isNull(config.getString(CONFIG_TRATE_EXCHANGE_NAME))) {
                throw new RuntimeException("configs key: " + CONFIG_TRATE_EXCHANGE_NAME + " no exists");
            }
            if (Objects.isNull(config.getJSONObject(CONFIG_TRATE_EXCHANGE_NAME).getString(CONFIG_EXCHANGE_NAME))) {
                throw new RuntimeException("configs key: " + CONFIG_TRATE_EXCHANGE_NAME + " no exists");
            }
            if (Objects.isNull(config.getJSONObject(CONFIG_TRATE_EXCHANGE_NAME).getString(CONFIG_ACCESSKEY_NAME))) {
                throw new RuntimeException("configs key: " + CONFIG_ACCESSKEY_NAME + " no exists");
            }
            if (Objects.isNull(config.getJSONObject(CONFIG_TRATE_EXCHANGE_NAME).getString(CONFIG_SECRETKEY_NAME))) {
                throw new RuntimeException("configs key: " + CONFIG_SECRETKEY_NAME + " no exists");
            }

            Class clazz = this.strategySources;
            Class superclass = clazz.getSuperclass();
            Object obj = clazz.newInstance();
            List<String> sub = new ArrayList<>();
            Arrays.asList(clazz.getDeclaredFields()).forEach( f -> {sub.add(f.getName());});
            List<String> sup = new ArrayList<>();
            Arrays.asList(superclass.getDeclaredFields()).forEach( f -> {sup.add(f.getName());});

            // 实例化属性
            Field fi = superclass.getDeclaredField(CONFIG_ATTRIBUTE);
            fi.setAccessible(true);
            fi.set(obj, config);
            for (Map.Entry<String, Object> p : config.entrySet()) {
                Field f;
                // 父类
                if (sup.contains(p.getKey())) {
                    f = superclass.getDeclaredField(p.getKey());
                    // 子类
                } else if (sub.contains(p.getKey())) {
                    f = clazz.getDeclaredField(p.getKey());
                } else {
                    continue;
//                    throw new RuntimeException("Reflection processing error");
                }
                f.setAccessible(true);
                Type t = f.getGenericType();
                if (IMdExchange.class.getTypeName().equals(t.getTypeName())) {
                    IMdExchange mdE =
                            MdExchangeFactory.createMdExchange(config.getJSONObject(f.getName()).getString(CONFIG_EXCHANGE_NAME),
                                    config.getJSONObject(f.getName()).getString(CONFIG_SECURITIESTYPE_NAME));
                    f.set(obj, mdE);
                } else if (ITradeExchange.class.getTypeName().equals(t.getTypeName())) {
                    ITradeExchange tradeE =
                            TradeExchangeFactory.createTradeExchange(config.getJSONObject(f.getName()).getString(CONFIG_EXCHANGE_NAME),
                                    config.getJSONObject(f.getName()).getString(CONFIG_SECURITIESTYPE_NAME),
                                    config.getJSONObject(CONFIG_TRATE_EXCHANGE_NAME).getString(CONFIG_ACCESSKEY_NAME),
                                    config.getJSONObject(CONFIG_TRATE_EXCHANGE_NAME).getString(CONFIG_SECRETKEY_NAME));
                    f.set(obj, tradeE);
                } else {
                    f.set(obj, p.getValue());
                }
            }

            // ioc Register
            if (obj != null) {
                if (this.beanDefinitionMap.containsKey(config.getString(CONFIG_STRATEGY_ID))) {
                    throw new RuntimeException("strategy id repeat in ioc container");
                }
                this.beanDefinitionMap.put(config.getString(CONFIG_STRATEGY_ID), obj);
            }
        }
    }

    private void handleAnnotation() {
        // 注册定时任务
        for (Map.Entry<String, Object> pair : this.beanDefinitionMap.entrySet()) {
            // 获取字节码文件对象
            Class clazz = pair.getValue().getClass();
            // 获取所有方法
            Method[] methods = clazz.getDeclaredMethods();
            for (Method method : methods) {
                // 判断是否有定时任务注解
                if (method.isAnnotationPresent(ScheduledByZyf.class)) {
                    method.setAccessible(true);
                    try {
                        ScheduledByZyf ann = method.getAnnotation(ScheduledByZyf.class);

                        /*cron方式定时*/
                        // Check cron expression
                        String cron = ann.cron();
                        // null、""、" "返回false
                        if (StringUtils.isNotBlank(cron)) {
                            ScheduledTask scheduledTask = ScheduledTask.newScheduledTask(pair.getValue(), method).
                                    newScheduler().
                                    setJobDetail().
                                    setCronTrigger(cron).
                                    build();
                            ScheduleTaskManager.getInstance().add(scheduledTask);
                        }

                        /*设置初始延迟时间：initialDelay和延迟轮询时间：fixedDelay方式定时*/
                        long initialDelay = ann.initialDelay();
                        if (initialDelay < 0) {
                            initialDelay = 0;
                        }
                        long fixedDelay = ann.fixedDelay();
                        if (fixedDelay >= 0) {
                            ScheduledTask scheduledTask = ScheduledTask.newScheduledTask(pair.getValue(), method).
                                    newScheduler().
                                    setJobDetail().
                                    setFixedDelayTrigger(initialDelay, fixedDelay).
                                    build();
                            ScheduleTaskManager.getInstance().add(scheduledTask);
                        }
                    } catch (Exception e) {
                        e.fillInStackTrace();
                        throw new RuntimeException();
                    }
                }
            }
        }

        // 注册行情接口
    }

    /**
     * 启动定时任务
     */
    private void startUpScheduler() {
        ScheduleTaskManager.getInstance().start();
    }

    /**
     * 加载配置文件
     * @param args
     * @return
     */
    public JSONObject prepareConfig(String[] args) {
        return Environment.prepareConfig(args);
    }
}
