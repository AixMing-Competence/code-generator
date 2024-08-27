package com.aixming.maker.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;
import java.util.List;

/**
 * @author AixMing
 * @since 2024-08-23 18:42:59
 */
@CommandLine.Command(name = "list", description = "查看文件列表", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("查看文件列表");
        String inputPath = System.getProperty("user.dir");
        List<File> files = FileUtil.loopFiles(inputPath);
        for (File file : files) {
            System.out.println(file);
        }
    }
}
