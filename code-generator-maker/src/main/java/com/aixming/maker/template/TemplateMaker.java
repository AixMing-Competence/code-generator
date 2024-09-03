package com.aixming.maker.template;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aixming.maker.enums.FileGenerateTypeEnum;
import com.aixming.maker.enums.FileTypeEnum;
import com.aixming.maker.meta.Meta;

import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * 模板制作工具
 *
 * @author AixMing
 * @since 2024-09-02 20:26:53
 */
public class TemplateMaker {

    public static void main(String[] args) {
        // 源项目路径
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "acm-template-pro-demo";
        String tempDirPath = projectPath + File.separator + ".temp";

        long snowflakeNextId = IdUtil.getSnowflakeNextId();
        String templatePath = tempDirPath + File.separator + snowflakeNextId;
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
        }

        // 复制目录，创建工作空间
        FileUtil.copy(originProjectPath, templatePath, true);

        // 一、输入信息
        // 1、项目基本信息
        String name = "acm-template-pro-generator";
        String description = "算法模板生成器";

        // 2、文件输入信息
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath));
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");
        String inputFilePath = "src/main/java/com/aixming/MainTemplate.java";
        String outputFilePath = inputFilePath + ".ftl";

        // 3、输入模型参数信息
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("outputText");
        modelInfo.setType("String");
        modelInfo.setDefaultValue("sum = ");

        // 二、替换字符串，生成模板文件
        String inputFileAbsolutePath = sourceRootPath + File.separator + inputFilePath;
        String fileContent = FileUtil.readUtf8String(inputFileAbsolutePath);
        // 替换源文件内容
        String replacement = String.format("${%s}", modelInfo.getFieldName());
        String newFileContent = StrUtil.replace(fileContent, "sum", replacement);
        // 输出模板文件
        String outputFileAbsolutePath = sourceRootPath + File.separator + outputFilePath;
        FileUtil.writeUtf8String(newFileContent, outputFileAbsolutePath);

        // 三、生成配置文件（meta.json）
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);

        Meta.FileConfig fileConfig = new Meta.FileConfig();
        meta.setFileConfig(fileConfig);

        fileConfig.setSourceRootPath(sourceRootPath);
        List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
        fileConfig.setFiles(fileInfoList);

        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfoList.add(fileInfo);
        fileInfo.setInputPath(inputFilePath);
        fileInfo.setOutputPath(outputFilePath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        Meta.ModelConfig modelConfig = new Meta.ModelConfig();
        meta.setModelConfig(modelConfig);
        List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
        modelConfig.setModels(modelInfoList);
        modelInfoList.add(modelInfo);
        System.out.println();

        String jsonStr = JSONUtil.toJsonPrettyStr(meta);
        FileUtil.writeUtf8String(jsonStr, metaOutputPath);

    }

}
