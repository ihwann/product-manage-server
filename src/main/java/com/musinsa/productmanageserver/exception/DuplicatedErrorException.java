package com.musinsa.productmanageserver.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicatedErrorException extends RuntimeException {

    private final HttpStatus httpStatus;
    private final String message;

    public DuplicatedErrorException(HttpStatus httpStatus, String message) {
        this.httpStatus = httpStatus;
        this.message = message;
    }
}
