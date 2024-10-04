package com.aixming.web.vertx;

import cn.hutool.json.JSONUtil;
import com.aixming.web.common.ResultUtils;
import com.aixming.web.controller.GeneratorController;
import com.aixming.web.manager.CacheManager;
import com.aixming.web.model.dto.generator.GeneratorQueryRequest;
import com.aixming.web.model.vo.GeneratorVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.vertx.core.AbstractVerticle;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.http.HttpServerResponse;

public class MainVerticle extends AbstractVerticle {

    private CacheManager cacheManager;

    public MainVerticle(CacheManager cacheManager) {
        this.cacheManager = cacheManager;
    }

    @Override
    public void start() throws Exception {
        // Create the HTTP server
        vertx.createHttpServer()
                // Handle every request using the router
                .requestHandler(req -> {
                    HttpMethod httpMethod = req.method();
                    String path = req.path();
                    if (HttpMethod.GET.equals(httpMethod) && "/generator/page".equals(path)) {
                        req.handler(buffer -> {
                            GeneratorQueryRequest generatorQueryRequest = JSONUtil.toBean(buffer.toString(), GeneratorQueryRequest.class);
                            // 先从缓存中获取
                            String cacheKey = GeneratorController.getPageCacheKey(generatorQueryRequest);
                            Object cacheValue = cacheManager.get(cacheKey);

                            HttpServerResponse response = req.response();
                            response.putHeader("content-type","application/json");

                            if (cacheValue != null) {
                                // 返回 json 响应
                                response.end(JSONUtil.toJsonStr(ResultUtils.success((Page<GeneratorVO>) cacheValue)));
                                return;
                            }

                            response.end("");
                        });
                    }
                })
                // Start listening
                .listen(8888)
                // Print the port on success
                .onSuccess(server ->
                        System.out.println(
                                "HTTP server started on port " + server.actualPort()
                        )
                );
    }
}