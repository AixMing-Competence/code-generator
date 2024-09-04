package com.aixming.maker.template.model;

import lombok.Data;

import java.util.List;

/**
 * @author AixMing
 * @since 2024-09-04 10:30:43
 */
@Data
public class TemplateMakerFileConfig {

    private List<FileInfoConfig> files;

    @Data
    public static class FileInfoConfig {
        private String path;
        private List<FileFilterConfig> filterConfigList;
    }
}
