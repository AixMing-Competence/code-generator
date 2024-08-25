package com.aixming.cli.pattern;

/**
 * @author AixMing
 * @since 2024-08-23 18:26:46
 */
public class Device {
    
    private String name;

    public Device(String name) {
        this.name = name;
    }

    public void turnOff(){
        System.out.println(name + "设备关闭了");
    }

    public void turnOn(){
        System.out.println(name + "设备打开了");
    }
    
}
