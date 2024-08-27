package com.aixming.maker.generator.file;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 文件生成
 *
 * @author AixMing
 * @since 2024-08-24 14:58:56
 */
public class FileGenerator {

    public static void doGenerate(Object model) throws TemplateException, IOException {
        // 先复制项目
        String projectPath = System.getProperty("user.dir");
        File parentFile = new File(projectPath).getParentFile();
        File copyFile = new File(parentFile, "samples");
        StaticFileGenerator.copyFilesByHutool(copyFile.getAbsolutePath(), projectPath);

        // 再重写
        String inputPath = projectPath + File.separator + "src/main/resources/templates/MainTemplate.java.ftl";
        String outputPath = projectPath + File.separator + "samples/src/main/java/org/example/MainTemplate.java";
        DynamicFileGenerator.doGenerator(inputPath, outputPath, model);
    }

}
