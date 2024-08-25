package com.aixming.generator;

import cn.hutool.captcha.generator.RandomGenerator;
import com.aixming.model.MainTemplateConfig;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;

/**
 * @author AixMing
 * @since 2024-08-21 21:10:12
 */
public class DynamicGenerator {

    public static void main(String[] args) throws IOException, TemplateException {
        String projectPath = System.getProperty("user.dir") + File.separator + "code-generator-basic";
        String inputPath = projectPath + File.separator + "src/main/resources/templates" + File.separator + "MainTemplate.java.ftl";
        String outputPath = projectPath + File.separator + "MainTemplate.java";

        // 准备数据
        MainTemplateConfig model = new MainTemplateConfig();
        model.setAuthor("AixMing");
        model.setOutputText("output message");
        model.setLoop(false);

        doGenerator(inputPath,outputPath,model);
    }

    public static void doGenerator(String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        File inputFile = new File(inputPath);
        File templateDir = inputFile.getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);

        configuration.setDefaultEncoding("utf-8");
        configuration.setNumberFormat("0.######");

        Template template = configuration.getTemplate(inputFile.getName());

        // 文件输出位置
        FileWriter out = new FileWriter(outputPath);
        template.process(model, out);
        out.close();
    }
}
