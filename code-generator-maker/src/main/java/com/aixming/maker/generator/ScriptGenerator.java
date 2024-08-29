package com.aixming.maker.generator;

import cn.hutool.core.io.FileUtil;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.nio.file.attribute.PosixFilePermissions;
import java.util.Set;

/**
 * 脚本文件生成
 *
 * @author AixMing
 * @since 2024-08-28 11:41:02
 */
public class ScriptGenerator {

    public static void doGenerate(String outputPath, String jarPath) {
        // Linux 脚本
        // #!/bin/bash
        // java -jar target/code-generator-basic-1.0-SNAPSHOT-jar-with-dependencies.jar "$@"
        StringBuilder sb = new StringBuilder();
        sb.append("#!/bin/bash").append("\n");
        sb.append(String.format("java -jar %s \"$@\"", jarPath)).append("\n");
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8), outputPath);
        // 添加可执行权限
        try {
            Set<PosixFilePermission> permissions = PosixFilePermissions.fromString("rwxrwxrwx");
            Files.setPosixFilePermissions(Paths.get(outputPath), permissions);
        } catch (Exception e) {
            // ignore
        }

        sb = new StringBuilder();
        // windows 脚本
        // @echo off
        // java -jar target/code-generator-basic-1.0-SNAPSHOT-jar-with-dependencies.jar %*
        sb.append("@echo off").append("\n");        
        sb.append(String.format("java -jar %s %%*", jarPath)).append("\n");
        FileUtil.writeBytes(sb.toString().getBytes(StandardCharsets.UTF_8), outputPath+".bat");
    }
}
