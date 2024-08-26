package com.aixming;

import com.aixming.cli.CommandExecutor;

/**
 * @author Duzeming
 * @since 2024-08-20 23:39:57
 */// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) {
        // args = new String[]{"generate","-l","-a","-o"};
        // args = new String[]{"config"};
        // args = new String[]{"list"};
        
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}
