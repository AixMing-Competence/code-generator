package com.aixming.web.model.vo;

import cn.hutool.json.JSONUtil;
import com.aixming.maker.meta.Meta;
import com.aixming.web.model.entity.Generator;
import lombok.Data;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * 代码生成器视图
 *
 * @author AixMing
 */
@Data
public class GeneratorVO implements Serializable {

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
     * 创建用户 id
     */
    private Long userId;

    /**
     * 状态
     */
    private Integer status;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 更新时间
     */
    private Date updateTime;

    /**
     * 创建用户信息
     */
    private UserVO user;

    /**
     * 封装类转对象
     *
     * @param generatorVO
     * @return
     */
    public static Generator voToObj(GeneratorVO generatorVO) {
        if (generatorVO == null) {
            return null;
        }
        Generator generator = new Generator();
        BeanUtils.copyProperties(generatorVO, generator);
        List<String> tags = generatorVO.getTags();
        Meta.FileConfig fileConfig = generatorVO.getFileConfig();
        Meta.ModelConfig modelConfig = generatorVO.getModelConfig();
        generator.setTags(JSONUtil.toJsonStr(tags));
        generator.setFileConfig(JSONUtil.toJsonStr(fileConfig));
        generator.setModelConfig(JSONUtil.toJsonStr(modelConfig));
        return generator;
    }

    /**
     * 对象转封装类
     *
     * @param generator
     * @return
     */
    public static GeneratorVO objToVo(Generator generator) {
        if (generator == null) {
            return null;
        }
        GeneratorVO generatorVO = new GeneratorVO();
        BeanUtils.copyProperties(generator, generatorVO);
        generatorVO.setTags(JSONUtil.toList(generator.getTags(), String.class));
        generatorVO.setFileConfig(JSONUtil.toBean(generator.getFileConfig(), Meta.FileConfig.class));
        generatorVO.setModelConfig(JSONUtil.toBean(generator.getModelConfig(), Meta.ModelConfig.class));
        return generatorVO;
    }
}
