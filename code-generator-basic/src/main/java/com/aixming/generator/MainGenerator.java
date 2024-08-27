package com.aixming.generator;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 核心代码生成
 *
 * @author AixMing
 * @since 2024-08-24 14:58:56
 */
public class MainGenerator {

    /**
     * 生成代码
     *
     * @param modelData 数据模型
     * @throws TemplateException
     * @throws IOException
     */
    public static void doGenerate(Object modelData) throws TemplateException, IOException {

        String inputRootPath = "D:\\IdeaProjects\\code-generator\\code-generator-basic";
        String outputRootPath = "D:\\IdeaProjects\\code-generator";

        String inputPath;
        String outputPath;

        // 动态文件用 freemarker 生成
        inputPath = new File(inputRootPath, "src/main/java/com/aixming/MainTemplate.java.ftl").getAbsolutePath();
        outputPath = new File(outputRootPath, "src/main/java/com/aixming/MainTemplate.java").getAbsolutePath();
        DynamicGenerator.doGenerator(inputPath, outputPath, modelData);

        // 静态文件直接复制
        inputPath = new File(inputRootPath, ".gitignore").getAbsolutePath();
        outputPath = new File(outputRootPath, ".gitignore").getAbsolutePath();
        StaticGenerator.copyFilesByHutool(inputPath, outputPath);

        inputPath = new File(inputRootPath, "README.md").getAbsolutePath();
        outputPath = new File(outputRootPath, "README.md").getAbsolutePath();
        StaticGenerator.copyFilesByHutool(inputPath, outputPath);
    }

}
