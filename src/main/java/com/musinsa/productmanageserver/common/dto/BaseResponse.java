package com.musinsa.productmanageserver.common.dto;

import lombok.Builder;

@Builder
public record BaseResponse<T>(String resultCode, T data) {

    public static final String SUCCESS = "SUCCESS";
    public static final String FAIL = "FAIL";
}
