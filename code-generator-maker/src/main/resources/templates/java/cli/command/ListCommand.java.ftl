package ${basePackage}.cli.command;

import cn.hutool.core.io.FileUtil;
import picocli.CommandLine;

import java.io.File;

/**
 * @author ${author}
 */
@CommandLine.Command(name = "list", description = "查看文件列表", mixinStandardHelpOptions = true)
public class ListCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("查看文件列表");
        String inputPath = System.getProperty("${fileConfig.inputRootPath}");
        for (File file : FileUtil.loopFiles(inputPath)) {
            System.out.println(file);
        }
    }
}
