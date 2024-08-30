package com.aixming.maker.meta;

/**
 * 元信息异常
 *
 * @author AixMing
 * @since 2024-08-30 10:11:47
 */
public class MetaException extends RuntimeException {
    public MetaException(String message) {
        super(message);
    }

    public MetaException(String message, Throwable cause) {
        super(message, cause);
    }
}
