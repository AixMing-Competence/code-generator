package com.aixming.web.model.dto.generator;

import com.aixming.maker.meta.Meta;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 创建代码生成器请求
 *
 * @author AixMing
 */
@Data
public class GeneratorAddRequest implements Serializable {

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
    private List<String> tags;

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

    /**
     * 状态
     */
    private Integer status;

    private static final long serialVersionUID = 1L;
    
}
