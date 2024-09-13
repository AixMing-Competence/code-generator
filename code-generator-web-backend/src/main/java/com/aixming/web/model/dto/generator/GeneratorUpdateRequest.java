package com.aixming.web.model.dto.generator;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 更新代码生成器请求（管理员更新）
 *
 * @author AixMing
 */
@Data
public class GeneratorUpdateRequest implements Serializable {

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
    private List<String> tags;

    /**
     * 文件配置（json 字符串）
     */
    private String fileConfig;

    /**
     * 模型配置（json 字符串）
     */
    private String modelConfig;

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