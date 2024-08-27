package com.aixming.maker.generator.file;

import cn.hutool.core.io.FileUtil;

/**
 * 静态文件（直接复制）
 * 
 * @author Duzeming
 * @since 2024-08-21 16:13:48
 */
public class StaticFileGenerator {

    /**
     * 拷贝文件
     *
     * @param inputPath  源文件
     * @param outputPath 目标文件
     */
    public static void copyFilesByHutool(String inputPath, String outputPath) {
        FileUtil.copy(inputPath, outputPath, false);
    }
    
}
