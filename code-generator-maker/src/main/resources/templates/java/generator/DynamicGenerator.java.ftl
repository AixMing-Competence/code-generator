package ${basePackage}.generator;

import cn.hutool.core.io.FileUtil;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * @author ${author}
 */
public class DynamicGenerator {

    public static void doGenerator(String inputPath, String outputPath, Object model) throws IOException, TemplateException {
        Configuration configuration = new Configuration(Configuration.VERSION_2_3_32);
        File inputFile = new File(inputPath);
        File templateDir = inputFile.getParentFile();
        configuration.setDirectoryForTemplateLoading(templateDir);

        configuration.setDefaultEncoding("utf-8");
        configuration.setNumberFormat("0.######");

        Template template = configuration.getTemplate(inputFile.getName());

        if(!FileUtil.exist(outputPath)){
            FileUtil.touch(outputPath);
        }

        // 文件输出位置
        FileWriter out = new FileWriter(outputPath, StandardCharsets.UTF_8);
        template.process(model, out);
        out.close();
    }

}
