package com.aixming.cli.pattern;

/**
 * @author AixMing
 * @since 2024-08-23 18:31:08
 */
public class TurnOnCommand implements Command{
    
    private Device device;

    public TurnOnCommand(Device device) {
        this.device = device;
    }

    @Override
    public void execute() {
        device.turnOn();
    }
}
