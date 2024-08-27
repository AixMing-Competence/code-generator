package ${basePackage}.maker.cli.command;

import cn.hutool.core.bean.BeanUtil;
import ${basePackage}.maker.generator.file.FileGenerator;
import ${basePackage}.maker.model.DataModel;
import lombok.Data;
import picocli.CommandLine;

import java.util.concurrent.Callable;

/**
 * @author ${author}
 */
@Data
@CommandLine.Command(name = "generate", description = "生成代码", mixinStandardHelpOptions = true)
public class GenerateCommand implements Callable<Integer> {
    
<#list modelConfig.models as modelInfo>
    <#if modelInfo.description??>
    /**
     * ${modelInfo.description}
     */
    </#if>
    @CommandLine.Option(names = {<#if modelInfo.abbr??>"-${modelInfo.abbr}", </#if>"--${modelInfo.fieldName}"}<#if modelInfo.description??>, description = "${modelInfo.description}"</#if>, arity = "0..1", interactive = true, echo = true)
    private ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
    
</#list>
    @Override
    public Integer call() throws Exception {
        DataModel mainTemplateConfig = new DataModel();
        BeanUtil.copyProperties(this, mainTemplateConfig);
        FileGenerator.doGenerate(mainTemplateConfig);
        return 0;
    }
}
