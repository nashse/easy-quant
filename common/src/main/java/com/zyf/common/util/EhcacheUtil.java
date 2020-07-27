package com.zyf.common.util;

import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.CacheManagerBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;

import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ehcache工具类
 *
 * @author yuanfeng.z
 * @date 2019/7/10 23:32
 */
public class EhcacheUtil {
    /**
     * 缓存管理器
     */
    private static CacheManager cacheManager = null;

    /**
     * 缓存map
     */
    private static Map<String, Cache> cacheMap = new ConcurrentHashMap<>();

    /**
     * 堆内内存对象存放个数
     */
    private static final Long HEAP_SIZE = 2L;

    /**
     * 缓存过期时间，默认1秒
     */
    private static final Long TIME_TO_LIVE_EXPIRATION = 3000L;

    static {
        EhcacheUtil.initCacheManager();
    }

    /**
     * 初始化缓存管理容器，单例
     * @return
     */
    public static CacheManager initCacheManager() {
        if (cacheManager == null) {
            synchronized (CacheManager.class) {
                if (cacheManager == null) {
                    cacheManager = CacheManagerBuilder.newCacheManagerBuilder().build(true);
                }
            }
        }
        return cacheManager;
    }

    /**
     * 创建cache
     * @param cacheName 缓存对象名称
     * @param k 键
     * @param v 值
     * @return
     */
    public static Cache createCache(String cacheName, Class k, Class v) {
        checkCacheManager();
        Cache cache;
        if (null == cacheManager.getCache(cacheName, k, v)) {
            cache = cacheManager.createCache(cacheName,
                    CacheConfigurationBuilder.newCacheConfigurationBuilder(k, v, ResourcePoolsBuilder.heap(HEAP_SIZE))
                            .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofMillis(TIME_TO_LIVE_EXPIRATION))));
            cacheMap.put(cacheName, cache);
        }
        return cacheMap.get(cacheName);
    }

    /**
     * 添加缓存
     * @param cacheName 缓存对象名称
     * @param key
     * @param value
     */
    public static void put(String cacheName, Object key, Object value) {
        Cache cache = cacheMap.get(cacheName);
        if (cache == null) {
            throw new IllegalArgumentException("没有该cache：" + cacheName);
        }
        cache.put(key, value);
        cacheMap.put(cacheName, cache);
    }

    /**
     * 获取cache
     * @param cacheName 缓存对象名称
     * @param key
     * @return
     */
    public static Object get(String cacheName, Object key) {
       Cache cache = cacheMap.get(cacheName);
       if (cache == null) {
           throw new IllegalArgumentException("没有该cacheName：" + cacheName);
       }
       Object value = cache.get(key);
       return value;
    }


//    /**
//     * 初始化缓存
//     *
//     * @param cacheName
//     *            缓存名称
//     * @param maxElementsInMemory
//     *            元素最大数量
//     * @param overflowToDisk
//     *            是否持久化到硬盘
//     * @param eternal
//     *            是否会死亡
//     * @param timeToLiveSeconds
//     *            缓存存活时间
//     * @param timeToIdleSeconds
//     *            缓存的间隔时间
//     * @return 缓存
//     * @throws Exception
//     */
//    public static Cache initCache(String cacheName, int maxElementsInMemory, boolean overflowToDisk, boolean eternal,
//                                  long timeToLiveSeconds, long timeToIdleSeconds) throws Exception {
//        try {
//            CacheManager singletonManager = CacheManager.create();
//            Cache myCache = singletonManager.getCache(cacheName);
//            if (myCache != null) {
//                CacheConfiguration config = cache.getCacheConfiguration();
//                config.setTimeToLiveSeconds(timeToLiveSeconds);
//                config.setMaxElementsInMemory(maxElementsInMemory);
//                config.setOverflowToDisk(overflowToDisk);
//                config.setEternal(eternal);
//                config.setTimeToIdleSeconds(timeToIdleSeconds);
//            }
//            if (myCache == null) {
//                Cache memoryOnlyCache = new Cache(cacheName, maxElementsInMemory, overflowToDisk, eternal, timeToLiveSeconds,
//                        timeToIdleSeconds);
//                singletonManager.addCache(memoryOnlyCache);
//                myCache = singletonManager.getCache(cacheName);
//            }
//            return myCache;
//        } catch (Exception e) {
//            throw new Exception("init cache " + cacheName + " failed!!!");
//        }
//    }

//    /**
//     * 初始化cache
//     *
//     * @param cacheName
//     *            cache的名字
//     * @param timeToLiveSeconds
//     *            有效时间
//     * @return cache 缓存
//     * @throws Exception
//     */
//    public static Cache initCache(String cacheName, long timeToLiveSeconds) throws Exception {
//        return EHCacheUtil.initCache(cacheName, EHCacheConfig.MAXELEMENTSINMEMORY, EHCacheConfig.OVERFLOWTODISK,
//                EHCacheConfig.ETERNAL, timeToLiveSeconds, EHCacheConfig.TIMETOIDLESECONDS);
//    }
//
//    /**
//     * 初始化Cache
//     *
//     * @param cacheName
//     *            cache容器名
//     * @return cache容器
//     * @throws Exception
//     */
//    public static Cache initMyCache(String cacheName) throws Exception {
//        return EHCacheUtil.initCache(cacheName, EHCacheConfig.TIMETOlIVESECONDS);
//    }
//
//    /**
//     * 修改缓存容器配置
//     *
//     * @param cacheName
//     *            缓存名
//     * @param timeToLiveSeconds
//     *            有效时间
//     * @param maxElementsInMemory
//     *            最大数量
//     * @throws Exception
//     */
//
//    public static boolean modifyCache(String cacheName, long timeToLiveSeconds, int maxElementsInMemory) throws Exception {
//        try {
//            if (StringUtils.isNotBlank(cacheName) && timeToLiveSeconds != 0L && maxElementsInMemory != 0) {
//                CacheManager myManager = CacheManager.create();
//                Cache myCache = myManager.getCache(cacheName);
//                CacheConfiguration config = myCache.getCacheConfiguration();
//                config.setTimeToLiveSeconds(timeToLiveSeconds);
//                config.setMaxElementsInMemory(maxElementsInMemory);
//                return true;
//            } else {
//                return false;
//            }
//        } catch (Exception e) {
//            throw new Exception("modify cache " + cacheName + " failed!!!");
//        }
//    }
//
//    /**
//     *
//     * 向指定容器中设置值
//     *
//     * @param vesselName
//     *            容器名
//     *
//     * @param key
//     *            键
//     *
//     * @param value
//     *            值
//     *
//     * @return 返回真
//     *
//     * @throws Exception
//     *             异常
//     */
//
//    public static boolean setValue(String cacheName, String key, Object value) throws Exception {
//        try {
//            CacheManager myManager = CacheManager.create();
//            Cache myCache = myManager.getCache(cacheName);
//            if (myCache == null) {
//                myCache = initCache(cacheName);
//            }
//            myCache.put(new Element(key, value));
//            return true;
//        } catch (Exception e) {
//            throw new Exception("set cache " + cacheName + " failed!!!");
//        }
//    }
//
//    /**
//     *
//     * 向指定容器中设置值
//     *
//     * @param cacheName
//     *            容器名
//     *
//     * @param key
//     *            键
//     *
//     * @param value
//     *            值
//     *
//     * @param timeToIdleSeconds
//     *            间歇时间
//     *
//     * @param timeToLiveSeconds
//     *            存活时间
//     *
//     * @return 真
//     *
//     * @throws Exception
//     *             抛出异常
//     */
//
//    public static boolean setValue(String cacheName, String key, Object value, Integer timeToLiveSeconds) throws Exception {
//        try {
//            CacheManager myManager = CacheManager.create();
//            Cache myCache = myManager.getCache(cacheName);
//            if (myCache == null) {
//                initCache(cacheName, timeToLiveSeconds);
//                myCache = myManager.getCache(cacheName);
//            }
//            myCache.put(new Element(key, value, EHCacheConfig.ETERNAL, EHCacheConfig.TIMETOIDLESECONDS, timeToLiveSeconds));
//            return true;
//        } catch (Exception e) {
//            throw new Exception("set cache " + cacheName + " failed!!!");
//        }
//    }
//
//    /**
//     *
//     * 从ehcache的指定容器中取值
//     *
//     * @createTime 2012-4-23
//     *
//     * @param key
//     *            键
//     *
//     * @return 返回Object类型的值
//     *
//     * @throws Exception
//     *             异常
//     */
//
//    public static Object getValue(String cacheName, String key) throws Exception {
//        try {
//            CacheManager myManager = CacheManager.create();
//            Cache myCache = myManager.getCache(cacheName);
//            if (myCache == null) {
//                myCache = initMyCache(cacheName);
//            }
//            return myCache.get(key).getValue();
//        } catch (Exception e) {
//            return null;
//        }
//    }
//
//    /**
//     *
//     * 删除指定的ehcache容器
//     *
//     * @param vesselName
//     *
//     * @return 真
//     *
//     * @throws Exception
//     *             失败抛出异常
//     */
//
//    public static boolean removeEhcache(String cacheName) throws Exception {
//        try {
//            CacheManager myManager = CacheManager.create();
//            myManager.removeCache(cacheName);
//            return true;
//        } catch (Exception e) {
//            throw new Exception("remove cache " + cacheName + " failed!!!");
//        }
//    }
//
//    /**
//     *
//     * 删除所有的EHCache容器
//     *
//     * @param cacheName
//     *            容器名
//     *
//     * @return 返回真
//     *
//     * @throws Exception
//     *             失败抛出异常
//     */
//
//    public static boolean removeAllEhcache(String cacheName) throws Exception {
//        try {
//            CacheManager myManager = CacheManager.create();
//            myManager.removalAll();
//            return true;
//        } catch (Exception e) {
//            throw new Exception("remove cache " + cacheName + " failed!!!");
//        }
//    }
//
//    /**
//     *
//     * 删除EHCache容器中的元素
//     *
//     * @param cacheName
//     *            容器名
//     *
//     * @param key
//     *            键
//     *
//     * @return 真
//     *
//     * @throws Exception
//     *             失败抛出异常
//     */
//
//    public static boolean removeElment(String cacheName, String key) throws Exception {
//        try {
//            CacheManager myManager = CacheManager.create();
//            Cache myCache = myManager.getCache(cacheName);
//            myCache.remove(key);
//            return true;
//        } catch (Exception e) {
//            throw new Exception("remove cache " + cacheName + " failed!!!");
//        }
//    }
//
//    /**
//     *
//     * 删除指定容器中的所有元素
//     *
//     * @param cacheName
//     *            容器名
//     *
//     * @param key
//     *            键
//     *
//     * @return 真
//     *
//     * @throws Exception
//     *             失败抛出异常
//     */
//
//    public static boolean removeAllElment(String cacheName, String key) throws Exception {
//        try {
//            CacheManager myManager = CacheManager.create();
//            Cache myCache = myManager.getCache(cacheName);
//            myCache.removeAll();
//            return true;
//        } catch (Exception e) {
//            throw new Exception("remove cache " + cacheName + " failed!!!");
//        }
//    }
//
//    /**
//     * 释放CacheManage
//     */
//
//    public static void shutdown() {
//        cacheManager.shutdown();
//    }
//
//    /**
//     * 移除cache
//     *
//     * @param cacheName
//     */
//
//    public static void removeCache(String cacheName) {
//        checkCacheManager();
//        cache = cacheManager.getCache(cacheName);
//        if (null != cache) {
//            cacheManager.removeCache(cacheName);
//        }
//    }
//
//    /**
//     * 移除cache中的key
//     *
//     * @param cacheName
//     */
//
//    public static void remove(String key) {
//        checkCache();
//        cache.remove(key);
//    }
//
//    /**
//     * 移除所有cache
//     */
//
//    public static void removeAllCache() {
//        checkCacheManager();
//        cacheManager.removalAll();
//    }
//
//    /**
//     *
//     * 移除所有Element
//     */
//
//    public static void removeAllKey() {
//        checkCache();
//        cache.removeAll();
//    }
//
//    /**
//     *
//     * 获取所有的cache名称
//     *
//     * @return
//     */
//
//    public static String[] getAllCaches() {
//        checkCacheManager();
//        return cacheManager.getCacheNames();
//    }
//
//    /**
//     *
//     * 获取Cache所有的Keys
//     *
//     * @return
//     */
//
//    public static List getKeys() {
//        checkCache();
//        return cache.getKeys();
//    }
//
//    /**
//     *
//     * 检测cacheManager
//     */

    private static void checkCacheManager() {
        if (null == cacheManager) {
            throw new IllegalArgumentException("调用前请先初始化CacheManager值：EhcacheUtil.initCacheManager");
        }
    }

//    public static void main(String[] arg) {
//        // 初始化--必须
//        EHCacheUtil.initCacheManager();
//        EHCacheUtil.initCache("cache");
//        EHCacheUtil.put("A", "AAAAA");
//        EHCacheUtil.put("B", "BBBBB");
//        EHCacheUtil.put("F", "FFFFF");
//        System.out.println(EHCacheUtil.get("F"));
//        List keys = EHCacheUtil.getKeys();
//        for (int i = 0; i < keys.size(); i++) {
//            System.out.println(keys.get(i));
//        }
//        EHCacheUtil.shutdown();
//    }
}