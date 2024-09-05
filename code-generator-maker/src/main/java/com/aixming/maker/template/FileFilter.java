package com.aixming.maker.template;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.FileUtil;
import com.aixming.maker.template.enums.FileFilterRangeEnum;
import com.aixming.maker.template.enums.FileFilterRuleEnum;
import com.aixming.maker.template.model.FileFilterConfig;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件过滤器
 *
 * @author AixMing
 * @since 2024-09-04 11:05:23
 */
public class FileFilter {

    public static List<File> doFilter(String filePath, List<FileFilterConfig> fileFilterConfigList) {
        List<File> files = FileUtil.loopFiles(filePath);
        if (CollUtil.isEmpty(fileFilterConfigList)) {
            return files;
        }
        return files.stream()
                .filter(file -> doSingleFileFilter(fileFilterConfigList, file))
                .collect(Collectors.toList());
    }

    /**
     * 单文件过滤
     *
     * @param fileFilterConfigList
     * @param file
     * @return
     */
    private static boolean doSingleFileFilter(List<FileFilterConfig> fileFilterConfigList, File file) {
        String fileName = file.getName();
        String fileContent = FileUtil.readUtf8String(file);

        // 遍历文件过滤范围和规则
        for (FileFilterConfig fileFilterConfig : fileFilterConfigList) {

            FileFilterRangeEnum fileFilterRangeEnum = FileFilterRangeEnum.getEnumByValue(fileFilterConfig.getRange());
            if (fileFilterRangeEnum == null) {
                continue;
            }

            String content = "";

            switch (fileFilterRangeEnum) {
                case FILE_NAME:
                    content = fileName;
                    break;
                case FILE_CONTENT:
                    content = fileContent;
                    break;
            }

            FileFilterRuleEnum fileFilterRuleEnum = FileFilterRuleEnum.getEnumByValue(fileFilterConfig.getRule());
            if (fileFilterRuleEnum == null) {
                continue;
            }

            boolean result = true;
            String value = fileFilterConfig.getValue();

            switch (fileFilterRuleEnum) {
                case CONTAINS:
                    result = content.contains(value);
                    break;
                case STARTSWITH:
                    result = content.startsWith(value);
                    break;
                case ENDSWITH:
                    result = content.endsWith(value);
                    break;
                case REGEX:
                    result = content.matches(value);
                    break;
                case EQUALS:
                    result = content.equals(value);
                    break;
            }

            if (!result) {
                return false;
            }
        }

        return true;
    }
}
