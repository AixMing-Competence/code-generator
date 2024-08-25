package com.aixming.cli.command;

import cn.hutool.core.util.ReflectUtil;
import com.aixming.model.MainTemplateConfig;
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
        Class<MainTemplateConfig> mainTemplateConfigClass = MainTemplateConfig.class;
        Field[] fields = ReflectUtil.getFields(mainTemplateConfigClass);
        for (Field field : fields) {
            System.out.println("字段类型：" + field.getType());
            System.out.println("字段名称：" + field.getName());
            System.out.println("--------");
        }
    }
}
