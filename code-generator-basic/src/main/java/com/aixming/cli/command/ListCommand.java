package com.aixming.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;

/**
 * @author AixMing
 * @since 2024-08-23 18:42:59
 */
@CommandLine.Command(name = "list", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable{
    @Override
    public void run() {
        String projectPath = System.getProperty("user.dir");
        File outputFile = new File(projectPath, "code-generator-demo/samples");
        for (File file : FileUtil.loopFiles(outputFile)) {
            System.out.println(file);
        }
    }
}
