package com.musinsa.productmanageserver.product.dto.external.response;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "LowestPriceBrandsByCategoryResponse", description = "카테고리별 최저가격 브랜드와 총액 조회 응답 정보")
public record LowestPriceBrandsByCategoryResponse(
    List<LowestPriceBrandByCategory> lowestPriceBrandList,
    String totalPrice
) {
}
