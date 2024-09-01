package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
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
    public static void doGenerate(DataModel modelData) throws TemplateException, IOException {

        String inputRootPath = "${fileConfig.inputRootPath}";
        String outputRootPath = "${fileConfig.outputRootPath}";

        String inputPath;
        String outputPath;

<#list modelConfig.models as modelInfo>
        ${modelInfo.type} ${modelInfo.fieldName} = modelData.${modelInfo.fieldName};
</#list>

<#list fileConfig.files as fileInfo>
        <#if fileInfo.condition??>
        if (${fileInfo.condition}) {
            inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
            outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
            <#if fileInfo.generateType == "dynamic">
            DynamicGenerator.doGenerator(inputPath, outputPath, modelData);
            <#else>
            StaticGenerator.copyFilesByHutool(inputPath, outputPath);
            </#if>
        }
        <#else>
        inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
        outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
        <#if fileInfo.generateType == "dynamic">
        DynamicGenerator.doGenerator(inputPath, outputPath, modelData);
        <#else>
        StaticGenerator.copyFilesByHutool(inputPath, outputPath);
        </#if>
        </#if>
    
</#list>
    }

}
