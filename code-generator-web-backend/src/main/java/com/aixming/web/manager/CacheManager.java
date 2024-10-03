package com.aixming.web.manager;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

/**
 * 多级缓存操作
 *
 * @author AixMing
 */
@Component
public class CacheManager {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 本地缓存
     */
    Cache<String, String> localCache = Caffeine.newBuilder()
            .expireAfterWrite(120, TimeUnit.MINUTES)
            .maximumSize(10_000)
            .build();

    /**
     * 写入缓存
     *
     * @param key
     * @param value
     */
    public void put(String key, String value) {
        localCache.put(key, value);
        stringRedisTemplate.opsForValue().set(key, value, 120, TimeUnit.MINUTES);
    }

    /**
     * 读缓存
     *
     * @param key
     * @return
     */
    public String get(String key) {
        // 先从本地缓存获取
        String value = localCache.getIfPresent(key);
        if (value != null) {
            return value;
        }
        // 本地缓存未命中，尝试从 redis 获取
        value = stringRedisTemplate.opsForValue().get(key);
        if (value != null) {
            // 将 redis 的值写入本地缓存
            localCache.put(key, value);
        }

        return value;
    }

    /**
     * 移除缓存
     *
     * @param key
     */
    public void delete(String key) {
        localCache.invalidate(key);
        stringRedisTemplate.delete(key);
    }

}
