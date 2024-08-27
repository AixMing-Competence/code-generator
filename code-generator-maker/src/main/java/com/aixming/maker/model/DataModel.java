package com.aixming.maker.model;

import lombok.Data;

/**
 * 动态模板配置
 *
 * @author Duzeming
 * @since 2024-08-21 20:43:03
 */
@Data
public class DataModel {

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
