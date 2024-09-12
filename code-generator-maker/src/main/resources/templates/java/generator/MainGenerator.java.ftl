package ${basePackage}.generator;

import ${basePackage}.model.DataModel;
import freemarker.template.TemplateException;

import java.io.File;
import java.io.IOException;

<#macro generateFile indent fileInfo>
${indent}inputPath = new File(inputRootPath, "${fileInfo.inputPath}").getAbsolutePath();
${indent}outputPath = new File(outputRootPath, "${fileInfo.outputPath}").getAbsolutePath();
<#if fileInfo.generateType == "dynamic">
${indent}DynamicGenerator.doGenerator(inputPath, outputPath, modelData);
<#else>
${indent}StaticGenerator.copyFilesByHutool(inputPath, outputPath);
</#if>
</#macro>

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
        <#if modelInfo.groupKey??>
        <#list modelInfo.models as modelInfoInner>
        ${modelInfoInner.type} ${modelInfoInner.fieldName} = modelData.${modelInfo.groupKey}.${modelInfoInner.fieldName};
        </#list>
        <#else>
        ${modelInfo.type} ${modelInfo.fieldName} = modelData.${modelInfo.fieldName};
        </#if>
</#list>

<#list fileConfig.files as fileInfo>
        <#if fileInfo.condition??>
        <#if fileInfo.groupKey??>
        // groupKey = ${fileInfo.groupKey}
        if (${fileInfo.condition}) {
        <#list fileInfo.files as fileInfo>
            <@generateFile fileInfo=fileInfo indent="            " />

        </#list>
        }
        <#else>
        if (${fileInfo.condition}) {
            <@generateFile fileInfo=fileInfo indent="            " />
        }
        </#if>
        <#else>
            <@generateFile fileInfo=fileInfo indent="        " />
        </#if>

</#list>
    }

}
