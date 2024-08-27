package com.aixming.maker.generator.file;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * 动态文件（用 freemarker 生成）
 *
 * @author AixMing
 * @since 2024-08-21 21:10:12
 */
public class DynamicFileGenerator {

    /**
     * 动态生成文件
     *
     * @param inputPath  输入路径
     * @param outputPath 输出路径
     * @param model      数据模型
     * @throws IOException
     * @throws TemplateException
     */
    public static void doGenerator(String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        File inputFile = new File(inputPath);
        File templateDir = inputFile.getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);

        configuration.setDefaultEncoding("utf-8");
        configuration.setNumberFormat("0.######");

        Template template = configuration.getTemplate(inputFile.getName());

        // 如果文件不存在，则创建文件
        if (!FileUtil.exist(outputPath)) {
            FileUtil.touch(outputPath);
        }

        // 文件输出位置
        FileWriter out = new FileWriter(outputPath, StandardCharsets.UTF_8);
        template.process(model, out);
        out.close();
    }
}
