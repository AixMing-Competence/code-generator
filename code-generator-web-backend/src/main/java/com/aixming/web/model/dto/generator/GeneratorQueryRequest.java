package com.aixming.web.model.dto.generator;

import com.aixming.web.common.PageRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

/**
 * 查询代码生成器请求
 *
 * @author AixMing
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GeneratorQueryRequest extends PageRequest implements Serializable {

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
     * 标签（json 数组）
     */
    private List<String> tags;

    /**
     * 至少一个标签
     */
    private List<String> orTag;

    /**
     * 代码生成器产物路径
     */
    private String distPath;

    /**
     * 创建用户 id
     */
    private Long userId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * id
     */
    private Long notId;

    /**
     * 搜索词
     */
    private String searchText;

    private static final long serialVersionUID = 1L;
}