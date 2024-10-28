package com.musinsa.productmanageserver.exception;

import lombok.Getter;

@Getter
public class DataParseException extends RuntimeException {

    private final String message;

    public DataParseException(String message) {
        this.message = message;
    }
}
