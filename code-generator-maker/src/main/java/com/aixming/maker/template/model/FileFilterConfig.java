package com.aixming.maker.template.model;

import lombok.Builder;
import lombok.Data;

/**
 * 文件过滤配置
 *
 * @author AixMing
 * @since 2024-09-04 10:22:43
 */
@Data
@Builder
public class FileFilterConfig {

    /**
     * 过滤范围
     */
    private String range;

    /**
     * 过滤规则
     */
    private String rule;

    /**
     * 过滤值
     */
    private String value;
}
