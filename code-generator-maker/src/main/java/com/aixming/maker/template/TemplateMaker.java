package com.aixming.maker.template;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONUtil;
import com.aixming.maker.enums.FileGenerateTypeEnum;
import com.aixming.maker.enums.FileTypeEnum;
import com.aixming.maker.meta.Meta;
import com.aixming.maker.template.enums.FileFilterRangeEnum;
import com.aixming.maker.template.enums.FileFilterRuleEnum;
import com.aixming.maker.template.model.FileFilterConfig;
import com.aixming.maker.template.model.TemplateMakerFileConfig;
import com.aixming.maker.template.model.TemplateMakerModelConfig;

import java.io.File;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 模板制作工具
 *
 * @author AixMing
 * @since 2024-09-02 20:26:53
 */
public class TemplateMaker {

    /**
     * 制作模板
     *
     * @param originProjectPath
     * @param templateMakerFileConfig
     * @param newMeta
     * @param templateMakerModelConfig
     * @param id                       区别状态
     * @return
     */
    private static long makeTemplate(String originProjectPath, TemplateMakerFileConfig templateMakerFileConfig, Meta newMeta, TemplateMakerModelConfig templateMakerModelConfig, Long id) {
        if (id == null) {
            id = IdUtil.getSnowflakeNextId();
        }

        // 源项目路径
        String projectPath = System.getProperty("user.dir");
        String tempDirPath = projectPath + File.separator + ".temp";

        String templatePath = tempDirPath + File.separator + id;
        if (!FileUtil.exist(templatePath)) {
            FileUtil.mkdir(templatePath);
            // 复制目录，创建工作空间
            FileUtil.copy(originProjectPath, templatePath, true);
        }

        // 一、输入信息

        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
                    BeanUtil.copyProperties(modelInfoConfig, modelInfo);
                    return modelInfo;
                }).collect(Collectors.toList());

        // 本次新增的模型列表
        ArrayList<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();

        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {
            String condition = modelGroupConfig.getCondition();
            String groupKey = modelGroupConfig.getGroupKey();
            String groupName = modelGroupConfig.getGroupName();

            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            modelInfo.setGroupKey(groupKey);
            modelInfo.setGroupName(groupName);
            modelInfo.setCondition(condition);
            // 模型全放到一个分组中
            modelInfo.setModels(inputModelInfoList);

            newModelInfoList = new ArrayList<>();
            newModelInfoList.add(modelInfo);
        } else {
            // 不分组，添加所有的模型信息到列表
            newModelInfoList.addAll(inputModelInfoList);
        }

        // 要挖坑的项目根目录
        String sourceRootPath = templatePath + File.separator + FileUtil.getLastPathEle(Paths.get(originProjectPath));
        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");

        ArrayList<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();

        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = templateMakerFileConfig.getFiles();

        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileInfoConfigList) {
            String inputFilePath = fileInfoConfig.getPath();
            String inputFileAbsolutePath = sourceRootPath + File.separator + inputFilePath;

            // 过滤文件
            List<File> fileList = FileFilter.doFilter(inputFileAbsolutePath, fileInfoConfig.getFilterConfigList());

            // 制作文件模板
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(templateMakerModelConfig, sourceRootPath, file);
                newFileInfoList.add(fileInfo);
            }
        }

        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = templateMakerFileConfig.getFileGroupConfig();
        if (fileGroupConfig != null) {
            String condition = fileGroupConfig.getCondition();
            String groupKey = fileGroupConfig.getGroupKey();
            String groupName = fileGroupConfig.getGroupName();

            Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
            fileInfo.setCondition(condition);
            fileInfo.setGroupKey(groupKey);
            fileInfo.setGroupName(groupName);
            fileInfo.setFiles(newFileInfoList);

            newFileInfoList = new ArrayList<>();
            newFileInfoList.add(fileInfo);
        }

        // 三、生成配置文件（meta.json）
        String metaOutputPath = sourceRootPath + File.separator + "meta.json";

        if (FileUtil.exist(metaOutputPath)) {
            // 追加配置参数
            newMeta = JSONUtil.toBean(FileUtil.readUtf8String(metaOutputPath), Meta.class);

            List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
            fileInfoList.addAll(newFileInfoList);

            List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            modelInfoList.addAll(newModelInfoList);

            newMeta.getFileConfig().setFiles(distinctFiles(fileInfoList));
            newMeta.getModelConfig().setModels(distinctModels(modelInfoList));

        } else {
            // 1、构造配置参数对象

            Meta.FileConfig fileConfig = new Meta.FileConfig();
            newMeta.setFileConfig(fileConfig);

            fileConfig.setSourceRootPath(sourceRootPath);
            List<Meta.FileConfig.FileInfo> fileInfoList = new ArrayList<>();
            fileConfig.setFiles(fileInfoList);
            fileInfoList.addAll(newFileInfoList);

            Meta.ModelConfig modelConfig = new Meta.ModelConfig();
            newMeta.setModelConfig(modelConfig);
            List<Meta.ModelConfig.ModelInfo> modelInfoList = new ArrayList<>();
            modelConfig.setModels(modelInfoList);
            modelInfoList.addAll(newModelInfoList);

        }

        // 输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);

        return id;
    }

    /**
     * 制作模板文件
     *
     * @param templateMakerModelConfig
     * @param sourceRootPath           尽量少用，可能是文件路径也可能是url等等，避免以后频繁修改
     * @param inputFile
     * @return
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath, File inputFile) {
        // 要挖坑的文件（注意一定要是相对路径）
        String inputFileAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");

        String inputFilePath = inputFileAbsolutePath.replace(sourceRootPath + "/", "");
        String outputFilePath = inputFilePath + ".ftl";

        // 二、替换字符串，生成模板文件
        String outputFileAbsolutePath = inputFile.getAbsolutePath() + ".ftl";
        String fileContent;

        // 如果已有模板文件，表示已经挖过坑，则在模板文件的基础上再挖坑
        if (FileUtil.exist(outputFileAbsolutePath)) {
            fileContent = FileUtil.readUtf8String(outputFileAbsolutePath);
        } else {
            fileContent = FileUtil.readUtf8String(inputFileAbsolutePath);
        }

        // 支持多个模型：对于同一个文件的内容，遍历模型进行多轮替换
        String newFileContent = fileContent;
        String replacement;
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        for (TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig : templateMakerModelConfig.getModels()) {
            String fieldName = modelInfoConfig.getFieldName();
            // 不是分组
            if (modelGroupConfig == null) {
                replacement = String.format("${%s}", fieldName);
            } else {
                // 是分组
                String groupKey = modelGroupConfig.getGroupKey();
                replacement = String.format("${%s.%s}", groupKey, fieldName);
            }
            newFileContent = StrUtil.replace(newFileContent, modelInfoConfig.getReplaceText(), replacement);
        }

        // 文件配置信息
        Meta.FileConfig.FileInfo fileInfo = new Meta.FileConfig.FileInfo();
        fileInfo.setInputPath(inputFilePath);
        fileInfo.setOutputPath(outputFilePath);
        fileInfo.setType(FileTypeEnum.FILE.getValue());

        // 如果文件内容没有被替换（挖坑），则静态生成
        if (fileContent.equals(newFileContent)) {
            fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
            fileInfo.setOutputPath(inputFilePath);
        } else {
            fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());
            // 输出模板文件
            FileUtil.writeUtf8String(newFileContent, outputFileAbsolutePath);
        }

        return fileInfo;
    }

    /**
     * 文件去重
     *
     * @param fileInfoList
     * @return
     */
    private static List<Meta.FileConfig.FileInfo> distinctFiles(List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 先将文件处理为有分组和无分组的
        // 先处理有分组的
        Map<String, List<Meta.FileConfig.FileInfo>> groupKeyFileInfoList = fileInfoList.stream()
                // 保留有 groupKey 的
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                // 按照 groupKey 的分为一组
                .collect(Collectors.groupingBy(Meta.FileConfig.FileInfo::getGroupKey));

        // 合并后的对象 map
        HashMap<String, Meta.FileConfig.FileInfo> groupKeyMergeFileInfoMap = new HashMap<>();

        // 取出相同组下的所有 FileInfo 进行去重，并将去重后的 newFileInfoList 设置到最新添加的带分组的 fileInfo中，并沿用其最新参数，保存该 fileInfo
        for (Map.Entry<String, List<Meta.FileConfig.FileInfo>> entry : groupKeyFileInfoList.entrySet()) {
            List<Meta.FileConfig.FileInfo> tempFileInfoList = entry.getValue();
            ArrayList<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>(tempFileInfoList.stream()
                    .flatMap(fileInfo -> fileInfo.getFiles().stream())
                    .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, fileInfo -> fileInfo, (e, r) -> r)).values());

            // 使用最新添加的 group 配置
            Meta.FileConfig.FileInfo newFileInfo = CollUtil.getLast(tempFileInfoList);
            newFileInfo.setFiles(newFileInfoList);

            String groupKey = entry.getKey();
            groupKeyMergeFileInfoMap.put(groupKey, newFileInfo);
        }

        ArrayList<Meta.FileConfig.FileInfo> resultFileInfoList = new ArrayList<>(groupKeyMergeFileInfoMap.values());

        // 处理无分组的
        // 筛选出无分组的，并进行去重
        resultFileInfoList.addAll(new ArrayList<>(fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isBlank(fileInfo.getGroupKey()))
                // 去重
                .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getInputPath, fileInfo -> fileInfo, (e, r) -> r))
                .values()));

        return resultFileInfoList;
    }

    /**
     * 模型去重
     *
     * @param modelInfoList
     * @return
     */
    private static List<Meta.ModelConfig.ModelInfo> distinctModels(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        // 先将模型处理为有分组和无分组的
        // 先处理有分组的
        Map<String, List<Meta.ModelConfig.ModelInfo>> groupKeyModelInfoList = modelInfoList.stream()
                // 保留有 groupKey 的
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                // 按照 groupKey 的分为一组
                .collect(Collectors.groupingBy(Meta.ModelConfig.ModelInfo::getGroupKey));

        // 合并后的对象 map
        HashMap<String, Meta.ModelConfig.ModelInfo> groupKeyMergeModelInfoMap = new HashMap<>();

        // 取出相同组下的所有 ModelInfo 进行去重，并将去重后的 newModelInfoList 设置到最新添加的带分组的 modelInfo中，并沿用其最新参数，保存该 modelInfo
        for (Map.Entry<String, List<Meta.ModelConfig.ModelInfo>> entry : groupKeyModelInfoList.entrySet()) {
            List<Meta.ModelConfig.ModelInfo> tempModelInfoList = entry.getValue();
            ArrayList<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>(tempModelInfoList.stream()
                    .flatMap(modelInfo -> modelInfo.getModels().stream())
                    .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, modelInfo -> modelInfo, (e, r) -> r)).values());

            // 使用最新添加的 group 配置
            Meta.ModelConfig.ModelInfo newModelInfo = CollUtil.getLast(tempModelInfoList);
            newModelInfo.setModels(newModelInfoList);

            String groupKey = entry.getKey();
            groupKeyMergeModelInfoMap.put(groupKey, newModelInfo);
        }

        ArrayList<Meta.ModelConfig.ModelInfo> resultModelInfoList = new ArrayList<>(groupKeyMergeModelInfoMap.values());

        // 处理无分组的
        // 筛选出无分组的，并进行去重
        resultModelInfoList.addAll(new ArrayList<>(modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isBlank(modelInfo.getGroupKey()))
                // 去重
                .collect(Collectors.toMap(Meta.ModelConfig.ModelInfo::getFieldName, modelInfo -> modelInfo, (e, r) -> r))
                .values()));

        return resultModelInfoList;
    }

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String originProjectPath = new File(projectPath).getParent() + File.separator + "springboot-init";
        String name = "springboot-init";
        String description = "springboot项目模板";
        Meta meta = new Meta();
        meta.setName(name);
        meta.setDescription(description);

        String inputFilePath1 = "src/main/java/com/aixming/springbootinit/common";
        String inputFilePath2 = "src/main/resources/application.yml";
        List<String> inputFilePathList = Arrays.asList(inputFilePath1, inputFilePath2);

        // 第一次挖坑
        // Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        // modelInfo.setFieldName("outputText");
        // modelInfo.setType("String");
        // modelInfo.setDefaultValue("sum");

        // 第二次挖坑
        Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
        modelInfo.setFieldName("className");
        modelInfo.setType("String");

        // 文件过滤配置
        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig2 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig2.setPath(inputFilePath2);

        TemplateMakerFileConfig.FileInfoConfig fileInfoConfig1 = new TemplateMakerFileConfig.FileInfoConfig();
        fileInfoConfig1.setPath(inputFilePath1);
        FileFilterConfig fileFilterConfig = FileFilterConfig.builder()
                .range(FileFilterRangeEnum.FILE_NAME.getValue())
                .rule(FileFilterRuleEnum.CONTAINS.getValue())
                .value("Base")
                .build();
        ArrayList<FileFilterConfig> fileFilterConfigList = new ArrayList<>();
        fileFilterConfigList.add(fileFilterConfig);
        fileInfoConfig1.setFilterConfigList(fileFilterConfigList);

        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = Arrays.asList(fileInfoConfig1, fileInfoConfig2);
        TemplateMakerFileConfig templateMakerFileConfig = new TemplateMakerFileConfig();
        TemplateMakerFileConfig.FileGroupConfig fileGroupConfig = new TemplateMakerFileConfig.FileGroupConfig();
        fileGroupConfig.setCondition("outputText");
        fileGroupConfig.setGroupKey("test");
        fileGroupConfig.setGroupName("测试分组");

        templateMakerFileConfig.setFileGroupConfig(fileGroupConfig);
        templateMakerFileConfig.setFiles(fileInfoConfigList);

        // 模型参数配置
        TemplateMakerModelConfig templateMakerModelConfig = new TemplateMakerModelConfig();
        // - 模型组配置
        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = new TemplateMakerModelConfig.ModelGroupConfig();
        modelGroupConfig.setGroupKey("mysql");
        modelGroupConfig.setGroupName("数据库配置");
        templateMakerModelConfig.setModelGroupConfig(modelGroupConfig);
        // - 模型配置
        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig1 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig1.setFieldName("url");
        modelInfoConfig1.setType("String");
        modelInfoConfig1.setDefaultValue("jdbc:mysql://localhost:3306/my_db");
        modelInfoConfig1.setReplaceText("jdbc:mysql://localhost:3306/my_db");

        TemplateMakerModelConfig.ModelInfoConfig modelInfoConfig2 = new TemplateMakerModelConfig.ModelInfoConfig();
        modelInfoConfig2.setFieldName("username");
        modelInfoConfig2.setType("String");
        modelInfoConfig2.setDefaultValue("root");
        modelInfoConfig2.setReplaceText("root");

        List<TemplateMakerModelConfig.ModelInfoConfig> modelInfoConfigList = Arrays.asList(modelInfoConfig1, modelInfoConfig2);
        templateMakerModelConfig.setModels(modelInfoConfigList);

        long id = makeTemplate(originProjectPath, templateMakerFileConfig, meta, templateMakerModelConfig, 1831593022337847296L);
        System.out.println(id);

    }

}
