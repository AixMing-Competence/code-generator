package com.aixming.maker.enums;

import cn.hutool.core.util.ObjectUtil;

/**
 * 文件过滤规则枚举
 *
 * @author AixMing
 * @since 2024-08-30 20:50:22
 */
public enum FileFilterRuleEnum {

    CONTAINS("包含", "contains"),
    STARTSWITH("前缀匹配", "startswith"),
    ENDSWITH("后缀匹配", "endswith"),
    REGEX("正则", "regex"),
    EQUALS("相等", "equals");

    private final String text;

    private final String value;

    FileFilterRuleEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public static FileFilterRuleEnum getEnumByValue(String value) {
        if (ObjectUtil.isEmpty(value)) {
            return null;
        }
        for (FileFilterRuleEnum anEnum : FileFilterRuleEnum.values()) {
            if (anEnum.value.equals(value)) {
                return anEnum;
            }
        }
        return null;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
