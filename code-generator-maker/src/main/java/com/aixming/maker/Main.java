package com.aixming.maker;

import com.aixming.maker.generator.GenerateTemplate;
import com.aixming.maker.generator.ZipGenerator;
import freemarker.template.TemplateException;

import java.io.IOException;

/**
 * @author Duzeming
 * @since 2024-08-20 23:39:57
 */
public class Main {
    public static void main(String[] args) throws TemplateException, IOException, InterruptedException {
        // GenerateTemplate generateTemplate = new MainGenerator();
        GenerateTemplate generateTemplate = new ZipGenerator();
        generateTemplate.doGenerate();
    }
    
}
