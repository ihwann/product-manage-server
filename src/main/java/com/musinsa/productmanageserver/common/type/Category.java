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
    OUTER("아우터"),
    BOTTOM("바지"),
    SNEAKERS("스니커즈"),
    BAG("가방"),
    HAT("모자"),
    SOCKS("양말"),
    ACCESSORY("액세서리");

    private final String description;

    public static Category findByName(String name) {
        for (Category category : values()) {
            if (category.name().equalsIgnoreCase(name)) {
                return category;
            }
        }
        throw new IllegalArgumentException("유효하지 않은 카테고리 입니다. : " + name);
    }
}
