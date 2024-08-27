package ${basePackage}.generator;

import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

/**
 * 核心代码生成
 *
 * @author ${author}
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

        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;

<#list fileConfig.files as fileInfo>
        inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
        outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
        <#if fileInfo.generateType == "dynamic">
        DynamicGenerator.doGenerator(inputPath, outputPath, modelData);
            
        <#else>
        StaticGenerator.copyFilesByHutool(inputPath, outputPath);
            
        </#if>
</#list>
    }

}
