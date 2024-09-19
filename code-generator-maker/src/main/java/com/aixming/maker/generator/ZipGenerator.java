package com.aixming.maker.generator;

/**
 * 生成代码生成器压缩包
 *
 * @author AixMing
 * @since 2024-09-19 17:03:43
 */
public class ZipGenerator extends GenerateTemplate {

    @Override
    protected String buildDist(String outputRootPath, String jarPath, String shellFilePath) {
        String buildDistPath = super.buildDist(outputRootPath, jarPath, shellFilePath);
        return super.buildZip(buildDistPath);
    }
    
}
