package com.aixming.maker.template.model;

import lombok.Data;

/**
 * @author AixMing
 * @since 2024-09-09 10:24:50
 */
@Data
public class TemplateMakerOutputConfig {

    /**
     * 是否从未分组的文件中移除组内的同名文件
     */
    private boolean removeGroupFilesFromRoot = true;

    /**
     * 是否从未分组的模型中移除组内的同名数组
     */
    private boolean removeGroupModelsFromRoot = true;
}
