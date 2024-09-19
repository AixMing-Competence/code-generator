package com.aixming.maker.generator;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.util.ZipUtil;
import com.aixming.maker.generator.file.DynamicFileGenerator;
import com.aixming.maker.meta.Meta;
import com.aixming.maker.meta.MetaManager;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * @author AixMing
 * @since 2024-08-31 10:00:48
 */
public abstract class GenerateTemplate {

    public void doGenerate() throws TemplateException, IOException, InterruptedException {
        Meta meta = MetaManager.getMetaObject();

        // 获取输出路径
        String projectPath = System.getProperty("user.dir");
        String outputRootPath = projectPath + File.separator + "generated" + File.separator + meta.getName();
        if (!FileUtil.exist(outputRootPath)) {
            FileUtil.mkdir(outputRootPath);
        }

        // 1. 复制原始文件到本项目中
        copySource(meta, outputRootPath);

        // 2. 代码生成
        generateCode(meta, outputRootPath);

        // 3. 构建 jar 包
        String jarPath = buildJar(outputRootPath, meta);

        // 4. 封装脚本
        String shellFilePath = buildScript(outputRootPath, jarPath);

        // 5. 生成精简版程序（产物包）
        buildDist(outputRootPath, jarPath, shellFilePath);
    }

    /**
     * 生成压缩文件
     *
     * @param outputPath
     * @return
     */
    protected String buildZip(String outputPath) {
        String zipPath = outputPath + ".zip";
        ZipUtil.zip(outputPath, zipPath);
        return zipPath;
    }

    /**
     * 生成精简版程序
     *
     * @param outputRootPath
     * @param jarPath
     * @param shellFilePath
     */
    protected String buildDist(String outputRootPath, String jarPath, String shellFilePath) {
        // 1. 先复制再删除（影响性能）
        // 2. 直接复制所需要的文件

        // 复制 jar 包
        String distOutputPath = outputRootPath + "-dist";
        String jarCopyPath = outputRootPath + File.separator + jarPath;
        String targetCopyPath = distOutputPath + File.separator + "target";
        FileUtil.mkdir(targetCopyPath);
        FileUtil.copy(jarCopyPath, targetCopyPath, true);

        // 复制模板
        FileUtil.copy(outputRootPath + File.separator + ".source", distOutputPath, true);

        // 复制脚本
        FileUtil.copy(shellFilePath, distOutputPath, true);
        FileUtil.copy(shellFilePath + ".bat", distOutputPath, true);

        return distOutputPath;
    }

    /**
     * 生成脚本
     *
     * @param outputRootPath
     * @param jarPath
     * @return
     */
    protected String buildScript(String outputRootPath, String jarPath) {
        String shellFilePath = outputRootPath + File.separator + "generator";
        ScriptGenerator.doGenerate(shellFilePath, jarPath);
        return shellFilePath;
    }

    /**
     * 生成 jar 包
     *
     * @param outputRootPath
     * @param meta
     * @return
     * @throws IOException
     * @throws InterruptedException
     */
    protected String buildJar(String outputRootPath, Meta meta) throws IOException, InterruptedException {
        // 构建 jar 包
        JarGenerator.doGenerate(outputRootPath);
        // target/code-generator-basic-1.0-SNAPSHOT-jar-with-dependencies.jar
        String jarName = String.format("%s-%s-jar-with-dependencies.jar", meta.getName(), meta.getVersion());
        String jarPath = "target" + File.separator + jarName;
        return jarPath;
    }

    /**
     * 根据模板文件生成代码
     *
     * @param meta
     * @param outputRootPath
     * @throws IOException
     * @throws TemplateException
     */
    protected void generateCode(Meta meta, String outputRootPath) throws IOException, TemplateException {
        // 读取resources文件
        ClassPathResource resourcesPath = new ClassPathResource("");
        String resourcesAbsolutePath = resourcesPath.getAbsolutePath();

        // java基础包路径
        String basePackage = String.join("/", meta.getBasePackage().split("\\."));
        String outputWithBasePackagePath = outputRootPath + File.separator + "src/main/java" + File.separator + basePackage;

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
        // inputPath = resourcesAbsolutePath + File.separator + "templates/README.md.ftl";
        // outputFilePath = outputRootPath + File.separator + "README.md";
        // DynamicFileGenerator.doGenerator(inputPath, outputFilePath, meta);
    }

    /**
     * 复制代码到本地（不污染源文件）
     *
     * @param meta
     * @param outputRootPath
     */
    protected void copySource(Meta meta, String outputRootPath) {
        // 将代码模板先复制到本地（可移植）
        String sourceRootPath = meta.getFileConfig().getSourceRootPath();
        String sourceCopyDestPath = outputRootPath + File.separator + ".source";
        FileUtil.copy(sourceRootPath, sourceCopyDestPath, false);
    }


}
