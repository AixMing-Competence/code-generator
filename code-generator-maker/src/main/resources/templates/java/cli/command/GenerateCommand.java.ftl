package ${basePackage}.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.generator.MainGenerator;
import ${basePackage}.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

<#--生成command-->
<#macro generateCommand indent modelInfo>
${indent}System.out.println("输入${modelInfo.groupName}配置：");
${indent}CommandLine commandLine = new CommandLine(${modelInfo.type}Command.class);
${indent}commandLine.execute(${modelInfo.allArgsStr});
</#macro>
<#--生成选项-->
<#macro generateOption indent modelInfo>
<#if modelInfo.description??>
${indent}/**
${indent} * ${modelInfo.description}
${indent} */
</#if>
${indent}@CommandLine.Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}", </#if>"--${modelInfo.fieldName}"}<#if modelInfo.description??>, description = "${modelInfo.description}"</#if>, arity = "0..1", interactive = true, echo = true)
${indent}private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#macro>
/**
 * @author ${author}
 */
@Data
@CommandLine.Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {

<#list modelConfig.models as modelInfo>
    <#if modelInfo.groupKey??>
    <#if modelInfo.groupName??>
    /**
     * ${modelInfo.groupName}
     */
    </#if>
    private static DataModel.${modelInfo.type} ${modelInfo.groupKey} = new DataModel.${modelInfo.type}();

    @Data
    @CommandLine.Command(name = "${modelInfo.groupKey}", description = "${modelInfo.description}")
    static class ${modelInfo.type}Command implements Callable<Integer> {
        
        <#list modelInfo.models as modelInfo>
        <@generateOption indent="        " modelInfo=modelInfo />

        </#list>
        @Override
        public Integer call() throws Exception {
        <#list modelInfo.models as subModelInfo>
            ${modelInfo.groupKey}.${subModelInfo.fieldName} = ${subModelInfo.fieldName};
        </#list>
            return 0;
        }

    }
    <#else>
    <@generateOption indent="    " modelInfo=modelInfo />
    </#if>
    
</#list>
    <#-- 生成调用方法 -->
    @Override
    public Integer call() throws Exception {
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        <#if modelInfo.condition??>
        if (${modelInfo.condition}) {
            <@generateCommand indent="            " modelInfo=modelInfo />
        }
        <#else>
        <@generateCommand indent="        " modelInfo=modelInfo />
        </#if>
        
        </#if>
        </#list>
        DataModel dataModel = new DataModel();
        BeanUtil.copyProperties(this, dataModel);
        <#list modelConfig.models as modelInfo>
        <#if modelInfo.groupKey??>
        dataModel.${modelInfo.groupKey} = ${modelInfo.groupKey};
        </#if>
        </#list>
        MainGenerator.doGenerate(dataModel);
        return 0;
    }
}
