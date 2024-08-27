package com.aixming.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import com.aixming.maker.generator.file.DynamicFileGenerator;
import com.aixming.maker.meta.Meta;
import com.aixming.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author AixMing
 * @since 2024-08-27 09:22:49
 */
public class MainGenerator {
    public static void main(String[] args) throws TemplateException, IOException {
        Meta meta = MetaManager.getMetaObject();

        // 获取输出路径
        String projectPath = System.getProperty("user.dir");
        String basePackagePath = String.join("/", meta.getBasePackage().split("\\."));
        String outputPath = projectPath + File.separator + "generated/src/main/java/" + basePackagePath;
        File outputPathFile = new File(outputPath);
        if (!FileUtil.exist(outputPathFile)) {
            FileUtil.mkdir(outputPathFile);
        }

        // 读取resources文件
        ClassPathResource resourcesPath = new ClassPathResource("");
        String resourcesAbsolutePath = resourcesPath.getAbsolutePath();

        // model.dataModel
        String inputPath = resourcesAbsolutePath + File.separator + "templates/java/model/DataModel.java.ftl";
        String outputFilePath = outputPath + File.separator + "model/DataModel.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // cli.command.generateCommand
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputPath + File.separator + "cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // cli.command.listCommand
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputPath + File.separator + "cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // cli.command.configCommand
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputPath + File.separator + "cli/command/ConfigCommand.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // cli.CommandExecutor
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputPath + File.separator + "cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // main
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/Main.java.ftl";
        outputFilePath = outputPath + File.separator + "Main.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // generator.file.DynamicGenerator
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/generator/file/DynamicGenerator.java.ftl";
        outputFilePath = outputPath + File.separator + "generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // generator.file.StaticFileGenerator
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/generator/file/StaticGenerator.java.ftl";
        outputFilePath = outputPath + File.separator + "generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // generator.file.MainGenerator
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputPath + File.separator + "generator/MainGenerator.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);
    }
}
