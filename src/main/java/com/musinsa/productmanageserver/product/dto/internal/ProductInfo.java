package com.musinsa.productmanageserver.product.dto.internal;

import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.product.model.ProductEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class ProductInfo {

    private Long productId; // 상품 ID
    private BrandInfo brandInfo; // 브랜드 정보
    private Category category; // 카테고리
    private String productName; // 상품 이름
    private Integer productPrice; // 상품 가격

    @Builder(builderClassName = "FromEntityBuilder", builderMethodName = "fromEntityBuilder")
    public ProductInfo(ProductEntity entity) {
        this.productId = entity.getId();
        this.brandInfo = BrandInfo.fromEntityBuilder()
            .entity(entity.getBrand())
            .build();
        this.category = entity.getCategory();
        this.productName = entity.getProductName();
        this.productPrice = entity.getProductPrice();
    }

    @Builder
    public ProductInfo(Long productId, BrandInfo brandInfo, Category category, String productName, Integer productPrice) {
        this.productId = productId;
        this.brandInfo = brandInfo;
        this.category = category;
        this.productName = productName;
        this.productPrice = productPrice;
    }
}
