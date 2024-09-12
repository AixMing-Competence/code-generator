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
import com.aixming.maker.template.model.TemplateMakerConfig;
import com.aixming.maker.template.model.TemplateMakerFileConfig;
import com.aixming.maker.template.model.TemplateMakerModelConfig;
import com.aixming.maker.template.model.TemplateMakerOutputConfig;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
     * @param templateMakerConfig
     * @return
     */
    public static long makerTemplate(TemplateMakerConfig templateMakerConfig) {
        Long id = templateMakerConfig.getId();
        Meta meta = templateMakerConfig.getMeta();
        String originProjectPath = templateMakerConfig.getOriginProjectPath();
        TemplateMakerFileConfig templateMakerFileConfig = templateMakerConfig.getFileConfig();
        TemplateMakerModelConfig templateMakerModelConfig = templateMakerConfig.getModelConfig();
        TemplateMakerOutputConfig outputConfig = templateMakerConfig.getOutputConfig();

        return makeTemplate(originProjectPath, templateMakerFileConfig, meta, templateMakerModelConfig, outputConfig, id);
    }

    /**
     * 制作模板
     *
     * @param originProjectPath
     * @param templateMakerFileConfig
     * @param newMeta
     * @param templateMakerModelConfig
     * @param templateMakerOutputConfig
     * @param id                        区别状态
     * @return
     */
    public static long makeTemplate(String originProjectPath, TemplateMakerFileConfig templateMakerFileConfig, Meta newMeta, TemplateMakerModelConfig templateMakerModelConfig, TemplateMakerOutputConfig templateMakerOutputConfig, Long id) {
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

        // 要挖坑的项目根目录
        String sourceRootPath = FileUtil.loopFiles(new File(templatePath), 1, null)
                .stream()
                .filter(File::isDirectory)
                .findFirst()
                // 可能过滤后为空，没有目录
                .orElseThrow(RuntimeException::new)
                .getAbsolutePath();

        sourceRootPath = sourceRootPath.replaceAll("\\\\", "/");

        // 制作文件模板
        ArrayList<Meta.FileConfig.FileInfo> newFileInfoList = makeFileTemplates(templateMakerFileConfig, templateMakerModelConfig, sourceRootPath);

        // 处理模型信息
        ArrayList<Meta.ModelConfig.ModelInfo> newModelInfoList = getModelInfoList(templateMakerModelConfig);

        // 三、生成配置文件（meta.json）
        String metaOutputPath = templatePath + File.separator + "meta.json";

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

        // 有无分组文件去重
        if (templateMakerOutputConfig != null) {
            if (templateMakerOutputConfig.isRemoveGroupFilesFromRoot()) {
                List<Meta.FileConfig.FileInfo> fileInfoList = newMeta.getFileConfig().getFiles();
                newMeta.getFileConfig().setFiles(TemplateMakerUtils.removeGroupFilesFromRoot(fileInfoList));
            }
            // if(templateMakerOutputConfig.isRemoveGroupModelsFromRoot()){
            //     List<Meta.ModelConfig.ModelInfo> modelInfoList = newMeta.getModelConfig().getModels();
            //     newMeta.getModelConfig().setModels(TemplateMakerUtils.removeGroupModelsFromRoot(modelInfoList));
            // }
        }

        // 输出元信息文件
        FileUtil.writeUtf8String(JSONUtil.toJsonPrettyStr(newMeta), metaOutputPath);

        return id;
    }

    /**
     * 获取模型配置
     *
     * @param templateMakerModelConfig
     * @return
     */
    private static ArrayList<Meta.ModelConfig.ModelInfo> getModelInfoList(TemplateMakerModelConfig templateMakerModelConfig) {
        // 本次新增的模型列表
        ArrayList<Meta.ModelConfig.ModelInfo> newModelInfoList = new ArrayList<>();
        // 非空校验
        if (templateMakerModelConfig == null) {
            return newModelInfoList;
        }

        List<TemplateMakerModelConfig.ModelInfoConfig> models = templateMakerModelConfig.getModels();
        if (CollUtil.isEmpty(models)) {
            return newModelInfoList;
        }

        List<Meta.ModelConfig.ModelInfo> inputModelInfoList = models.stream()
                .map(modelInfoConfig -> {
                    Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
                    BeanUtil.copyProperties(modelInfoConfig, modelInfo);
                    return modelInfo;
                }).collect(Collectors.toList());

        TemplateMakerModelConfig.ModelGroupConfig modelGroupConfig = templateMakerModelConfig.getModelGroupConfig();
        if (modelGroupConfig != null) {
            Meta.ModelConfig.ModelInfo modelInfo = new Meta.ModelConfig.ModelInfo();
            BeanUtil.copyProperties(modelGroupConfig, modelInfo);

            // 模型全放到一个分组中
            modelInfo.setModels(inputModelInfoList);

            // newModelInfoList = new ArrayList<>();
            newModelInfoList.add(modelInfo);
        } else {
            // 不分组，添加所有的模型信息到列表
            newModelInfoList.addAll(inputModelInfoList);
        }
        return newModelInfoList;
    }

    /**
     * 生成多个文件
     *
     * @param templateMakerFileConfig
     * @param templateMakerModelConfig
     * @param sourceRootPath
     * @return
     */
    private static ArrayList<Meta.FileConfig.FileInfo> makeFileTemplates(TemplateMakerFileConfig templateMakerFileConfig, TemplateMakerModelConfig templateMakerModelConfig, String sourceRootPath) {
        ArrayList<Meta.FileConfig.FileInfo> newFileInfoList = new ArrayList<>();
        // 非空校验
        if (templateMakerFileConfig == null) {
            return newFileInfoList;
        }

        List<TemplateMakerFileConfig.FileInfoConfig> fileInfoConfigList = templateMakerFileConfig.getFiles();
        if (CollUtil.isEmpty(fileInfoConfigList)) {
            return newFileInfoList;
        }

        for (TemplateMakerFileConfig.FileInfoConfig fileInfoConfig : fileInfoConfigList) {
            String inputFilePath = fileInfoConfig.getPath();
            String inputFileAbsolutePath = sourceRootPath + File.separator + inputFilePath;

            // 过滤文件
            List<File> fileList = FileFilter.doFilter(inputFileAbsolutePath, fileInfoConfig.getFilterConfigList());

            // 不处理已生成的 ftl 文件
            fileList = fileList.stream()
                    .filter(file -> !file.getAbsolutePath().endsWith(".ftl"))
                    .collect(Collectors.toList());

            // 制作文件模板
            for (File file : fileList) {
                Meta.FileConfig.FileInfo fileInfo = makeFileTemplate(templateMakerModelConfig, sourceRootPath, file, fileInfoConfig);
                newFileInfoList.add(fileInfo);
            }
        }

        // 如果是文件组
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
        return newFileInfoList;
    }

    /**
     * 制作模板文件
     *
     * @param templateMakerModelConfig
     * @param sourceRootPath           尽量少用，可能是文件路径也可能是url等等，避免以后频繁修改
     * @param inputFile
     * @return
     */
    private static Meta.FileConfig.FileInfo makeFileTemplate(TemplateMakerModelConfig templateMakerModelConfig,
                                                             String sourceRootPath,
                                                             File inputFile,
                                                             TemplateMakerFileConfig.FileInfoConfig fileInfoConfig) {
        // 要挖坑的文件（注意一定要是相对路径）
        String inputFileAbsolutePath = inputFile.getAbsolutePath().replaceAll("\\\\", "/");

        String inputFilePath = inputFileAbsolutePath.replace(sourceRootPath + "/", "");
        String outputFilePath = inputFilePath + ".ftl";

        // 二、替换字符串，生成模板文件
        String outputFileAbsolutePath = inputFile.getAbsolutePath() + ".ftl";
        String fileContent;

        // 如果已有模板文件，表示已经挖过坑，则在模板文件的基础上再挖坑
        boolean existTemplate = FileUtil.exist(outputFileAbsolutePath);
        if (existTemplate) {
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
        fileInfo.setInputPath(outputFilePath);
        fileInfo.setOutputPath(inputFilePath);
        fileInfo.setCondition(fileInfoConfig.getCondition());
        fileInfo.setType(FileTypeEnum.FILE.getValue());
        fileInfo.setGenerateType(FileGenerateTypeEnum.DYNAMIC.getValue());

        // 如果文件内容没有被替换（挖坑），则静态生成
        boolean contentEquals = fileContent.equals(newFileContent);
        // 如果没有模板文件，并且内容没有改变，才去静态生成
        if (!existTemplate) {
            if (contentEquals) {
                fileInfo.setGenerateType(FileGenerateTypeEnum.STATIC.getValue());
                fileInfo.setInputPath(inputFilePath);
            } else {
                FileUtil.writeUtf8String(newFileContent, outputFileAbsolutePath);
            }
        } else if (!contentEquals) {
            // 有模板文件，并且增加了新坑，则更新模板文件
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
                    .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, fileInfo -> fileInfo, (e, r) -> r)).values());

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
                .collect(Collectors.toMap(Meta.FileConfig.FileInfo::getOutputPath, fileInfo -> fileInfo, (e, r) -> r))
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

}
