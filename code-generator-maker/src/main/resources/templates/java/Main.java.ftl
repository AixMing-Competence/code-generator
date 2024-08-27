package ${basePackage}.maker;

import ${basePackage}.maker.cli.CommandExecutor;

/**
 * @author ${author}
 */
public class Main {
    public static void main(String[] args) {
        CommandExecutor commandExecutor = new CommandExecutor();
        commandExecutor.doExecute(args);
    }
}
