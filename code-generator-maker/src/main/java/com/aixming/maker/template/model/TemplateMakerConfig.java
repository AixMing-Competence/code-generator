package com.aixming.maker.template.model;

import com.aixming.maker.meta.Meta;
import lombok.Data;

/**
 * 模板制作配置
 *
 * @author AixMing
 * @since 2024-09-06 21:47:39
 */
@Data
public class TemplateMakerConfig {

    private Long id;

    private Meta meta = new Meta();

    private String originProjectPath;

    private TemplateMakerFileConfig fileConfig = new TemplateMakerFileConfig();

    private TemplateMakerModelConfig modelConfig = new TemplateMakerModelConfig();

    private TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();

}
