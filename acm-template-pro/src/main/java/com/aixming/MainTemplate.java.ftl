package com.aixming;

/**
 * ACM 输入模板（多数之和）
 * @author ${mainTemplate.author!""}
 */
public class MainTemplateConfig {
    public static void main(String[] args) {
<#if loop>
        while (true) {
</#if>
            for (int i = 1; i <= 5; i++) {
                System.out.println("${mainTemplate.outputText!} = " + i);
            }
<#if loop>
        }
</#if>
    }
}
