package com.aixming.web.vertx;

import com.aixming.web.manager.CacheManager;
import io.vertx.core.Vertx;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;

/**
 * @author AixMing
 * @since 2024-10-04 17:58:55
 */
@Component
public class VertxManager {

    @Resource
    private CacheManager cacheManager;

    @PostConstruct
    public void init() {
        Vertx vertx = Vertx.vertx();
        MainVerticle mainVerticle = new MainVerticle(cacheManager);
        vertx.deployVerticle(mainVerticle);
    }

}
