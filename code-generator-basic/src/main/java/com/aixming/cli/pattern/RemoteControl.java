package com.aixming.cli.pattern;

/**
 * @author AixMing
 * @since 2024-08-23 18:36:14
 */
public class RemoteControl {
    
    private Command command;

    public RemoteControl() {
        this.command = command;
    }

    public Command getCommand() {
        return command;
    }

    public void setCommand(Command command) {
        this.command = command;
    }

    public void pressButton(){
        command.execute();
    }
}
