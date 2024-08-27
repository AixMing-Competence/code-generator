package com.aixming.maker.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.aixming.maker.model.DataModel;
import picocli.CommandLine;

import java.lang.reflect.Field;

/**
 * @author AixMing
 * @since 2024-08-23 18:43:20
 */
@CommandLine.Command(name = "config", mixinStandardHelpOptions = true)
public class ConfigCommand implements Runnable {
    @Override
    public void run() {
        System.out.println("查看参数信息");
        Field[] fields = ReflectUtil.getFields(DataModel.class);
        for (Field field : fields) {
            System.out.println("字段类型：" + field.getType());
            System.out.println("字段名称：" + field.getName());
            System.out.println("--------");
        }
    }
}
