package com.aixming.cli.example;

import picocli.CommandLine;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.util.Arrays;
import java.util.concurrent.Callable;

@Command(name = "login", mixinStandardHelpOptions = true, version = "login 1.0")
class Login implements Callable<Integer> {

    @Option(names = {"-u", "--username"}, description = "用户名", interactive = true,prompt = "请输入用户名")
    String username;

    @Option(names = {"-p", "--password"}, description = "用户密码", interactive = true)
    String password;

    @Override
    public Integer call() throws Exception { // 实现你的业务逻辑
        System.out.println("password = " + password);
        System.out.println("username = " + username);
        return 0;
    }

    // this example implements Callable, so parsing, error handling and handling user
    // requests for usage help or version help can be done with one line of code.
    public static void main(String[] args) {
        new CommandLine(new Login()).execute("-u","-p");
    }
}
