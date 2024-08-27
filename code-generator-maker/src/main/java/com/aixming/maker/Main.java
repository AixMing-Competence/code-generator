package com.aixming.maker;

import com.aixming.maker.cli.CommandExecutor;

/**
 * @author Duzeming
 * @since 2024-08-20 23:39:57
 */
public class Main {
    public static void main(String[] args) {
        // args = new String[]{"generate","-l","-a","-o"};
        // args = new String[]{"config"};
        // args = new String[]{"list"};

        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}
