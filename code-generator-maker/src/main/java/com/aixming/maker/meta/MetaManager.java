package com.aixming.maker.meta;

import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;

/**
 * @author AixMing
 * @since 2024-08-26 21:18:33
 */
public class MetaManager {

    /**
     * 单例
     */
    private static volatile Meta meta;

    public static Meta getMetaObject() {
        if (meta == null) {
            synchronized (MetaManager.class) {
                if (meta == null) {
                    meta = initMeta();
                }
            }
        }
        return meta;
    }

    private static Meta initMeta() {
        String metaJson = ResourceUtil.readUtf8Str("meta.json");
        Meta meta = JSONUtil.toBean(metaJson, Meta.class);
        // todo 校验配置文件，处理默认值
        return meta;
    }
}
