package com.aixming.maker.template;

import cn.hutool.core.util.StrUtil;
import com.aixming.maker.meta.Meta;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 模本制作工具类
 *
 * @author AixMing
 * @since 2024-09-09 10:42:38
 */
public class TemplateMakerUtils {

    /**
     * 是否从未分组的文件中移除组内的同名文件
     *
     * @param fileInfoList
     * @return
     */
    public static List<Meta.FileConfig.FileInfo> removeGroupFilesFromRoot(List<Meta.FileConfig.FileInfo> fileInfoList) {
        // 获取所有有分组的文件
        List<Meta.FileConfig.FileInfo> groupFileInfoList = fileInfoList.stream()
                .filter(fileInfo -> StrUtil.isNotBlank(fileInfo.getGroupKey()))
                .collect(Collectors.toList());

        // 获取所有有分组的文件的 outputPath
        Set<String> outputPathSet = groupFileInfoList.stream()
                .flatMap(fileInfo -> fileInfo.getFiles().stream())
                .map(fileInfo -> fileInfo.getOutputPath())
                .collect(Collectors.toSet());

        // 移除没分组的，并且与组内文件有相同 outputPath 的文件
        return fileInfoList.stream()
                .filter(fileInfo -> {
                    if (StrUtil.isNotBlank(fileInfo.getGroupKey())) {
                        return true;
                    }
                    return !outputPathSet.contains(fileInfo.getOutputPath());
                }).collect(Collectors.toList());
    }

    public static List<Meta.ModelConfig.ModelInfo> removeGroupModelsFromRoot(List<Meta.ModelConfig.ModelInfo> modelInfoList) {
        // 获取所有有分组的模型
        List<Meta.ModelConfig.ModelInfo> groupModelInfoList = modelInfoList.stream()
                .filter(modelInfo -> StrUtil.isNotBlank(modelInfo.getGroupKey()))
                .collect(Collectors.toList());

        // 获取所有有分组的模型中的 fieldName
        Set<String> fieldNameSet = groupModelInfoList.stream()
                .flatMap(modelInfo -> modelInfo.getModels().stream())
                .map(modelInfo -> modelInfo.getFieldName())
                .collect(Collectors.toSet());

        // 移除没有分组的模型中的同名 fieldName
        return modelInfoList.stream()
                .filter(modelInfo -> {
                    // 有分组的直接保留
                    if (StrUtil.isNotBlank(modelInfo.getGroupKey())) {
                        return true;
                    }
                    return !fieldNameSet.contains(modelInfo.getFieldName());
                }).collect(Collectors.toList());
    }
}
