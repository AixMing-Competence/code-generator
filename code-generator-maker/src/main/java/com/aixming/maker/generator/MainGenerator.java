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
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMetaObject();

        // 获取输出路径
        String projectPath = System.getProperty("user.dir");
        String outputRootPath = projectPath + File.separator + "generated" + File.separator + meta.getName();
        if (!FileUtil.exist(outputRootPath)) {
            FileUtil.mkdir(outputRootPath);
        }
        
        // 将代码模板先复制到本地（可移植）
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputRootPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath,sourceCopyDestPath,false);

        // 读取resources文件
        ClassPathResource resourcesPath = new ClassPathResource("");
        String resourcesAbsolutePath = resourcesPath.getAbsolutePath();

        // java基础包路径
        String basePackage = String.join("/", meta.getBasePackage().split("\\."));
        String outputWithBasePackagePath = outputRootPath + File.separator+ "src/main/java"+ File.separator + basePackage;

        // model.dataModel
        String inputPath = resourcesAbsolutePath + File.separator + "templates/java/model/DataModel.java.ftl";
        String outputFilePath = outputWithBasePackagePath + File.separator + "model/DataModel.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // cli.command.generateCommand
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/cli/command/GenerateCommand.java.ftl";
        outputFilePath = outputWithBasePackagePath + File.separator + "cli/command/GenerateCommand.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // cli.command.listCommand
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/cli/command/ListCommand.java.ftl";
        outputFilePath = outputWithBasePackagePath + File.separator + "cli/command/ListCommand.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // cli.command.configCommand
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/cli/command/ConfigCommand.java.ftl";
        outputFilePath = outputWithBasePackagePath + File.separator + "cli/command/ConfigCommand.java.";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // cli.CommandExecutor
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/cli/CommandExecutor.java.ftl";
        outputFilePath = outputWithBasePackagePath + File.separator + "cli/CommandExecutor.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // main
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/Main.java.ftl";
        outputFilePath = outputWithBasePackagePath + File.separator + "Main.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // generator.DynamicGenerator
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/generator/DynamicGenerator.java.ftl";
        outputFilePath = outputWithBasePackagePath + File.separator + "generator/DynamicGenerator.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // generator.StaticFileGenerator
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/generator/StaticGenerator.java.ftl";
        outputFilePath = outputWithBasePackagePath + File.separator + "generator/StaticGenerator.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // generator.MainGenerator
        inputPath = resourcesAbsolutePath + File.separator + "templates/java/generator/MainGenerator.java.ftl";
        outputFilePath = outputWithBasePackagePath + File.separator + "generator/MainGenerator.java";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // pom.xml
        inputPath = resourcesAbsolutePath + File.separator + "templates/pom.xml.ftl";
        outputFilePath = outputRootPath + File.separator + "pom.xml";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);
        
        // README.md
        inputPath = resourcesAbsolutePath + File.separator + "templates/README.md.ftl";
        outputFilePath = outputRootPath + File.separator + "README.md";
        DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);

        // 构建 jar 包
        JarGenerator.doGenerate(outputRootPath);

        // 封装脚本
        // target/code-generator-basic-1.0-SNAPSHOT-jar-with-dependencies.jar
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = "target" + File.separator + jarName;
        ScriptGenerator.doGenerate(outputRootPath + File.separator + "generator", jarPath);
    }
}
