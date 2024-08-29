package com.aixming;

/**
 * ACM 输入模板（多数之和）
 * @author ${author}
 */
public class MainTemplateConfig {
    public static void main(String[] args) {
<#if loop>
        while (true) {
</#if>
            for (int i = 1; i <= 5; i++) {
                System.out.println("${outputText} = " + i);
            }
<#if loop>
        }
</#if>
    }
}
