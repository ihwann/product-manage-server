package com.musinsa.productmanageserver.product.dto.external.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BrandInsertRequest", description = "브랜드 추가 요청 정보")
public record BrandInsertRequest(
    String brandName
) {

}
