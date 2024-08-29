package com.aixming.maker.generator;

import java.io.*;

/**
 * 构建 jar 包
 *
 * @author AixMing
 * @since 2024-08-27 21:54:49
 */
public class JarGenerator {

    public static void doGenerate(String projectPath) throws IOException, InterruptedException {
        String winMavenCommand = "mvn.cmd clean package -DskipTests=true";
        String otherMavenCommand = "mvn clean package -DskipTests=true";
        String mavenCommand = winMavenCommand;
        ProcessBuilder processBuilder = new ProcessBuilder(mavenCommand.split(" "));
        processBuilder.directory(new File(projectPath));

        Process process = processBuilder.start();

        // 打印命令输出信息
        InputStream inputStream = process.getInputStream();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            System.out.println(line);
        }

        int exitCode = process.waitFor();
        System.out.println("Process finished with exit code " + exitCode);
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        doGenerate("D:\\IdeaProjects\\code-generator\\code-generator-demo");
    }
}
