package com.aixming.maker.enums;

/**
 * 文件类型枚举
 *
 * @author AixMing
 * @since 2024-08-30 20:50:22
 */
public enum FileTypeEnum {

    DIR("目录", "dir"),
    FILE("文件", "file");

    private final String text;

    private final String value;

    FileTypeEnum(String text, String value) {
        this.text = text;
        this.value = value;
    }

    public String getText() {
        return text;
    }

    public String getValue() {
        return value;
    }
}
