package com.musinsa.productmanageserver.product.dto.internal;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductUpdateDto {
    private Long productId;
    private Integer productPrice;

    @Builder
    public ProductUpdateDto(Long productId, Integer productPrice) {
        this.productId = productId;
        this.productPrice = productPrice;
    }
}
