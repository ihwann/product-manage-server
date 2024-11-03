package com.musinsa.productmanageserver.product.dto.internal;

import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.product.dto.external.request.ProductInsertRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductInsertDto {

    Long brandId;   // 브랜드 ID
    Category category; // 카테고리
    String productName; // 상품명
    Integer productPrice; // 상품 가격

    @Builder(builderClassName = "FromRequestBuilder", builderMethodName = "fromRequestBuilder")
    public ProductInsertDto(ProductInsertRequest request) {
        this.brandId = request.brandId();
        this.category = request.category();
        this.productName = request.productName();
        this.productPrice = request.productPrice();
    }

    @Builder
    public ProductInsertDto(Long brandId, Category category, String productName, Integer productPrice) {
        this.brandId = brandId;
        this.category = category;
        this.productName = productName;
        this.productPrice = productPrice;
    }
}
