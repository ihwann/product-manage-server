package com.musinsa.productmanageserver.product.dto.external.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BrandPriceResponse", description = "브랜드별 가격 조회 응답 정보")
public record BrandPriceResponse(
    String brandName,
    Integer price
) {

}
