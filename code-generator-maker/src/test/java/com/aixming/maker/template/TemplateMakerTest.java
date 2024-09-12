package com.aixming.maker.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.json.JSONUtil;
import com.aixming.maker.meta.Meta;
import com.aixming.maker.template.model.TemplateMakerConfig;
import com.aixming.maker.template.model.TemplateMakerFileConfig;
import com.aixming.maker.template.model.TemplateMakerModelConfig;
import com.aixming.maker.template.model.TemplateMakerOutputConfig;
import org.junit.Test;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author AixMing
 * @since 2024-09-06 14:01:58
 */
public class TemplateMakerTest {

    @Test
    public void makeTemplateTestBug1() {
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "springboot-init";
        String name = "springboot-init";
        String description = "springboot项目模板";
        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);

        String inputFilePath = "src/main/java/com/aixming/springbootinit/common";

        // 文件过滤配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig.setPath(inputFilePath);

        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = Arrays.asList(fileInfoConfig);
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        templateMakerFileConfig.setFiles(fileInfoConfigList);

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        // - 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setReplaceText("BaseResponse");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();
        outputConfig.setRemoveGroupFilesFromRoot(true);

        long id = TemplateMaker.makeTemplate(originProjectPath, templateMakerFileConfig, meta, templateMakerModelConfig, outputConfig, 1L);
        System.out.println(id);

    }

    @Test
    public void makeTemplateTestBug2() {
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "springboot-init";
        String name = "springboot-init";
        String description = "springboot项目模板";
        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);

        String inputFilePath = "./";

        // 文件过滤配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig.setPath(inputFilePath);

        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = Arrays.asList(fileInfoConfig);
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        templateMakerFileConfig.setFiles(fileInfoConfigList);

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        // - 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("className");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setReplaceText("BaseResponse");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        TemplateMakerOutputConfig outputConfig = new TemplateMakerOutputConfig();
        outputConfig.setRemoveGroupFilesFromRoot(true);

        long id = TemplateMaker.makeTemplate(originProjectPath, templateMakerFileConfig, meta, templateMakerModelConfig, outputConfig, 1L);
        System.out.println(id);

    }

    @Test
    public void testMakeTemplateWithJson() {
        String templateMakerJsonStr = ResourceUtil.readUtf8Str("templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(templateMakerJsonStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makerTemplate(templateMakerConfig);
        System.out.println(id);
    }

    /**
     * 制作 Spring Boot 项目模板
     */
    @Test
    public void makeSpringBootTemplate() {
        String rootPath = "examples/springboot-init";
        String configStr = ResourceUtil.readUtf8Str(rootPath + File.separator + "templateMaker.json");
        TemplateMakerConfig templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        long id = TemplateMaker.makerTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + File.separator + "templateMaker1.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makerTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + File.separator + "templateMaker2.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makerTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + File.separator + "templateMaker3.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makerTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + File.separator + "templateMaker4.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makerTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + File.separator + "templateMaker5.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makerTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + File.separator + "templateMaker6.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makerTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + File.separator + "templateMaker7.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makerTemplate(templateMakerConfig);

        configStr = ResourceUtil.readUtf8Str(rootPath + File.separator + "templateMaker8.json");
        templateMakerConfig = JSONUtil.toBean(configStr, TemplateMakerConfig.class);
        TemplateMaker.makerTemplate(templateMakerConfig);
        System.out.println(id);
    }

    @Override
    public void run() {
        System.out.println("查看文件列表");
        String inputRootPath = System.getProperty("${fileConfig.inputRootPath}");
        File inputRootFilePath = new File(inputRootPath);
        for (File file : FileUtil.loopFiles(inputRootFilePath)) {
            System.out.println(file);
        }
    }
}
