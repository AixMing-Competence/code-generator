package com.aixming.web.model.dto.generator;

import com.aixming.web.meta.Meta;
import lombok.Data;

import java.io.Serializable;

/**
 * 编辑代码生成器请求（用户编辑）
 *
 * @author AixMing
 */
@Data
public class GeneratorEditRequest implements Serializable {

    /**
     * id
     */
    private Long id;

    /**
     * 名称
     */
    private String name;

    /**
     * 描述
     */
    private String description;

    /**
     * 基础包
     */
    private String basePackage;

    /**
     * 版本
     */
    private String version;

    /**
     * 作者
     */
    private String author;

    /**
     * 图片
     */
    private String picture;

    /**
     * 标签（json 数组）
     */
    private String tags;

    /**
     * 文件配置（json 字符串）
     */
    private Meta.FileConfig fileConfig;

    /**
     * 模型配置（json 字符串）
     */
    private Meta.ModelConfig modelConfig;

    /**
     * 代码生成器产物路径
     */
    private String distPath;

    private static final long serialVersionUID = 1L;
    
}
