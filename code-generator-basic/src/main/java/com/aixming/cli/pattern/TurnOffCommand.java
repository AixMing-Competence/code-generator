package com.aixming.cli.pattern;

/**
 * @author AixMing
 * @since 2024-08-23 18:31:08
 */
public class TurnOffCommand implements Command{
    
    private Device device;

    public TurnOffCommand(Device device) {
        this.device = device;
    }

    @Override
    public void execute() {
        device.turnOff();
    }
}
