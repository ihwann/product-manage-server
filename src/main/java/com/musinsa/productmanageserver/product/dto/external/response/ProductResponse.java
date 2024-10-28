package com.musinsa.productmanageserver.product.dto.external.response;

import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.product.dto.internal.BrandInfo;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProductResponse", description = "상품 정보")
public record ProductResponse(
    Long productId,
    String productName,
    BrandInfo bradInfo,
    Category category,
    Integer price
) {
    public static ProductResponse from(ProductInfo productInfo) {
        return new ProductResponse(
            productInfo.getProductId(),
            productInfo.getProductName(),
            productInfo.getBrandInfo(),
            productInfo.getCategory(),
            productInfo.getProductPrice()
        );
    }
}
