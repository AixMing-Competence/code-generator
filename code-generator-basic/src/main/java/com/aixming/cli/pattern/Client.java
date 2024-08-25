package com.aixming.cli.pattern;

/**
 * @author AixMing
 * @since 2024-08-23 18:32:18
 */
public class Client {
    public static void main(String[] args) {
        Command turnOnCommand = new TurnOnCommand(new Device("海思"));
        Command turnOffCommand = new TurnOffCommand(new Device("蔚来"));

        // 遥控
        RemoteControl remoteControl = new RemoteControl();
        
        remoteControl.setCommand(turnOnCommand);
        remoteControl.pressButton();
        
        remoteControl.setCommand(turnOffCommand);
        remoteControl.pressButton();
        
    }
}
