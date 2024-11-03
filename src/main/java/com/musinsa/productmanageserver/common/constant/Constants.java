package com.musinsa.productmanageserver.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Constants {

    @NoArgsConstructor(access = AccessLevel.PRIVATE)
    public static final class ErrorMessage {
        public static final String NOT_FOUND_BRAND = "존재하지 않는 브랜드 입니다.";
        public static final String NOT_FOUND_PRODUCT = "존재하지 않는 상품 입니다.";
        public static final String INVALID_CATEGORY = "유효하지 않은 카테고리 입니다.";
    }
}
