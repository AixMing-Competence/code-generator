package com.aixming.model;

import lombok.Data;

/**
 * @author Duzeming
 * @since 2024-08-21 20:43:03
 */
@Data
public class MainTemplateConfig {

    /**
     * 作者（填充值）
     */
    private String author;

    /**
     * 输出信息
     */
    private String outputText;

    /**
     * 是否循环（开关）
     */
    private boolean loop;
}
 