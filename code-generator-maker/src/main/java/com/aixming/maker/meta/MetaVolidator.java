package com.aixming.maker.meta;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import com.aixming.maker.enums.FileGenerateTypeEnum;
import com.aixming.maker.enums.FileTypeEnum;
import com.aixming.maker.enums.ModelTypeEnum;

import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 元信息校验器
 *
 * @author AixMing
 * @since 2024-08-30 10:13:42
 */
public class MetaVolidator {

    public static void doVolidateAndFill(Meta meta) {
        validAndFillMetaRoot(meta);
        validAndFillFileConfig(meta);
        validAndFillModelConfig(meta);
    }

    private static void validAndFillModelConfig(Meta meta) {
        // modelConfig 校验和默认值填充
        Meta.ModelConfig modelConfig = meta.getModelConfig();
        if (modelConfig == null) {
            return;
        }
        List<Meta.ModelConfig.ModelInfo> modelInfoList = modelConfig.getModels();
        if (CollUtil.isEmpty(modelInfoList)) {
            return;
        }
        for (Meta.ModelConfig.ModelInfo modelInfo : modelInfoList) {
            // 为 group，不校验
            if (StrUtil.isNotEmpty(modelInfo.getGroupKey())) {
                // 生成中间参数
                String allArgsStr = modelInfo.getModels().stream()
                        .map(subModelInfo -> String.format("\"--%s\"", subModelInfo.getFieldName()))
                        .collect(Collectors.joining(", "));
                modelInfo.setAllArgsStr(allArgsStr);
                continue;
            }
            String fieldName = modelInfo.getFieldName();
            if (StrUtil.isBlank(fieldName)) {
                throw new MetaException("fieldName 是必填项");
            }
            String type = modelInfo.getType();
            if (StrUtil.isBlank(type)) {
                modelInfo.setType(ModelTypeEnum.STRING.getValue());
            }
        }
    }

    private static void validAndFillFileConfig(Meta meta) {
        // fileConfig 校验和默认值填充
        Meta.FileConfig fileConfig = meta.getFileConfig();
        if (fileConfig == null) {
            return;
        }
        String sourceRootPath = fileConfig.getSourceRootPath();
        if (StrUtil.isBlank(sourceRootPath)) {
            throw new MetaException("sourceRootPath 是必填项");
        }

        String inputRootPath = fileConfig.getInputRootPath();
        String projectName = FileUtil.getLastPathEle(Paths.get(sourceRootPath)).getFileName().toString();
        if (StrUtil.isEmpty(inputRootPath)) {
            inputRootPath = ".source/" + projectName;
            fileConfig.setInputRootPath(inputRootPath);
        }

        String outputRootPath = fileConfig.getOutputRootPath();
        if (StrUtil.isEmpty(outputRootPath)) {
            outputRootPath = "generated/" + projectName;
            fileConfig.setOutputRootPath(outputRootPath);
        }

        String type = fileConfig.getType();
        if (StrUtil.isEmpty(type)) {
            fileConfig.setType(FileTypeEnum.DIR.getValue());
        }

        List<Meta.FileConfig.FileInfo> files = fileConfig.getFiles();
        if (CollUtil.isEmpty(files)) {
            return;
        }
        for (Meta.FileConfig.FileInfo fileInfo : files) {
            if (StrUtil.isNotBlank(fileInfo.getGroupKey())) {
                continue;
            }

            // inputPath 必填项
            String inputPath = fileInfo.getInputPath();
            if (StrUtil.isBlank(inputPath)) {
                throw new MetaException("inputPath 是必填项");
            }

            String outputPath = fileInfo.getOutputPath();
            if (StrUtil.isEmpty(outputPath)) {
                // 如果后缀是ftl，就去掉
                if (inputPath.endsWith("ftl")) {
                    outputPath = inputPath.replace(".ftl", "");
                } else {
                    outputPath = inputPath;
                }
            }

            // 没有后缀为目录，有后缀为文件
            if (StrUtil.isBlank(FileUtil.getSuffix(inputPath))) {
                fileInfo.setType(FileTypeEnum.DIR.getValue());
            } else {
                fileInfo.setType(FileTypeEnum.FILE.getValue());
            }

            // 模板动态生成，文件静态生成
            String generateType = fileInfo.getGenerateType();
            if (StrUtil.isBlank(generateType)) {
                if (inputPath.endsWith("ftl")) {
                    fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
                } else {
                    fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
                }
            }
        }
    }

    private static void validAndFillMetaRoot(Meta meta) {
        // 基本信息校验和默认值填充
        String name = StrUtil.blankToDefault(meta.getName(), "my-generator");
        String description = StrUtil.emptyToDefault(meta.getDescription(), "我的代码生成器");
        String basePackage = StrUtil.blankToDefault(meta.getBasePackage(), "com.aixming");
        String version = StrUtil.blankToDefault(meta.getVersion(), "1.0");
        String author = StrUtil.emptyToDefault(meta.getAuthor(), "AixMing");
        String createTime = StrUtil.emptyToDefault(meta.getCreateTime(), DateUtil.now());

        meta.setName(name);
        meta.setDescription(description);
        meta.setBasePackage(basePackage);
        meta.setVersion(version);
        meta.setAuthor(author);
        meta.setCreateTime(createTime);
    }
}
