package com.aixming.generator;

import cn.hutool.Hutool;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ArrayUtil;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

/**
 * @author Duzeming
 * @since 2024-08-21 16:13:48
 */
public class StaticGenerator {

    public static void main(String[] args) {
        String projectPath = System.getProperty("user.dir");
        String inputPath = projectPath + File.separator + "code-generator-demo" + File.separator + "src";
        copyFilesByRecursive(inputPath, projectPath);
    }

    /**
     * 拷贝文件
     *
     * @param inputPath  源文件
     * @param outputPath 目标文件
     */
    public static void copyFilesByHutool(String inputPath, String outputPath) {
        FileUtil.copy(inputPath, outputPath, false);
    }

    /**
     * 递归复制文件
     *
     * @param inputPath
     * @param outputPath
     */
    public static void copyFilesByRecursive(String inputPath, String outputPath) {
        File inputFile = new File(inputPath);
        File outputFile = new File(outputPath);
        try {
            copyFileByRecursive(inputFile, outputFile);
        } catch (IOException e) {
            System.err.println("复制文件失败");
            e.printStackTrace();
        }
        FileUtil.copy(inputPath, outputPath, false);
    }

    /**
     * 复制文件
     *
     * @param inputFile
     * @param outputFile
     */
    public static void copyFileByRecursive(File inputFile, File outputFile) throws IOException {
        // 区分是目录还是文件
        if (inputFile.isDirectory()) {
            // 如果是目录
            File destFile = new File(outputFile, inputFile.getName());
            if (!destFile.exists()) {
                destFile.mkdirs();
            }
            // 获取目录下所有文件和子目录
            File[] fileList = inputFile.listFiles();
            if (ArrayUtil.isEmpty(fileList)) {
                return;
            }
            for (File file : fileList) {
                // 递归拷贝下一层文件
                copyFileByRecursive(file, destFile);
            }
        } else {
            // 如果是文件
            Path destPath = outputFile.toPath().resolve(inputFile.getName());
            Files.copy(inputFile.toPath(), destPath, StandardCopyOption.REPLACE_EXISTING);
        }
    }


}
