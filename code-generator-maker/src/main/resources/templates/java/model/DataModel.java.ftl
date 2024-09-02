package ${basePackage}.model;

import lombok.Data;

<#macro generate indent modelInfo>
<#if modelInfo.description??>
${indent}/**
${indent} * ${modelInfo.description}
${indent} */
</#if>
${indent}public ${modelInfo.type} ${modelInfo.fieldName}<#if modelInfo.defaultValue??> = ${modelInfo.defaultValue?c}</#if>;
</#macro>
/**
 * 数据模型
 */
@Data
public class DataModel {

<#list modelConfig.models as modelInfo>
    <#-- 有分组 -->
    <#if modelInfo.groupKey??>
    <#if modelInfo.groupName??>
    /**
     * ${modelInfo.groupName}
     */
    </#if>
    public ${modelInfo.type} ${modelInfo.groupKey} = new ${modelInfo.type}();

    <#if modelInfo.description??>
    /**
     * ${modelInfo.description}
     */
    </#if>
    @Data
    public static class ${modelInfo.type} {

        <#list modelInfo.models as modelInfo>
        <@generate modelInfo=modelInfo indent="        " />
        
        </#list>
    }

    <#else>
    <@generate modelInfo=modelInfo indent="    " />
    
    </#if>
</#list>
}
