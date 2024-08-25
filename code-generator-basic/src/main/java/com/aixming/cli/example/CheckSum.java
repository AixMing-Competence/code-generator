package com.aixming.cli.example;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.File;
import java.math.BigInteger;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.concurrent.Callable;

@Command(name = "checksum", mixinStandardHelpOptions = true, version = "checksum 4.0")
class CheckSum implements Callable<Integer> {

    @Option(names = {"-s", "--font-size"}, description = "Font size")
    int fontSize = 19;

    @Parameters(paramLabel = "<word>")
    private String[] words = {"Hello", "picocli"};

    @Override
    public Integer call() throws Exception { // 实现你的业务逻辑
        System.out.println("Font size = " + fontSize);
        System.out.println("words = " + Arrays.toString(words));
        return 0;
    }

    // this example implements Callable, so parsing, error handling and handling user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String[] args) {
        new CommandLine(new CheckSum()).execute(args);
    }
}
