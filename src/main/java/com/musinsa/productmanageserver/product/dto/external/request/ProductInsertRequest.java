package com.musinsa.productmanageserver.product.dto.external.request;

import com.musinsa.productmanageserver.common.type.Category;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProductInsertRequest", description = "상품 추가 요청 정보")
public record ProductInsertRequest(
    Long brandId,   // 브랜드 ID
    Category category, // 카테고리
    String productName, // 상품명
    Integer productPrice // 상품 가격
) {

}
