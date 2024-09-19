package com.aixming.maker.generator;

/**
 * @author AixMing
 * @since 2024-08-27 09:22:49
 */
public class MainGenerator extends GenerateTemplate{

    @Override
    protected String buildDist(String outputRootPath, String jarPath, String shellFilePath) {
        System.out.println("不要给我生成 dist");
        return "";
    }
    
}
