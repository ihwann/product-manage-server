package com.musinsa.productmanageserver.common.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 상품 카테고리 타입
 */
@Getter
@AllArgsConstructor
public enum Category {
    TOP("상의"),
    BOTTOM("하의"),
    OUTER("아우터"),
    SHOES("신발"),
    SOCKS("양말"),
    HAT("모자"),
    BAG("가방"),
    ACCESSORY("액세서리");

    private final String description;
}
