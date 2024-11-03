package com.musinsa.productmanageserver.product.dto.external.request;

import com.musinsa.productmanageserver.common.type.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;

@Schema(name = "ProductInsertRequest", description = "상품 추가 요청 정보")
public record ProductInsertRequest(
    @NotNull
    Long brandId,   // 브랜드 ID
    @NotNull
    Category category, // 카테고리
    @NotNull
    String productName, // 상품명
    @PositiveOrZero
    Integer productPrice // 상품 가격
) {

}
