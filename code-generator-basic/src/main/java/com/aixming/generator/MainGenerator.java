package com.aixming.generator;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author AixMing
 * @since 2024-08-24 14:58:56
 */
public class MainGenerator {
    
    public static void doGenerate(Object model) throws TemplateException, IOException {
        // 先复制项目
        String projectPath = System.getProperty("user.dir");
        String inputFilePath = projectPath + File.separator + "code-generator-demo/samples";
        StaticGenerator.copyFilesByRecursive(inputFilePath,projectPath);
        
        // 再重写
        String inputPath = projectPath + File.separator + "code-generator-basic/src/main/resources/templates/MainTemplate.java.ftl";
        String outputPath = projectPath + File.separator + "samples/src/main/java/org/example/MainTemplate.java";
        DynamicGenerator.doGenerator(inputPath,outputPath,model);
    }
}
