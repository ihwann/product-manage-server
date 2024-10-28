package com.musinsa.productmanageserver.product.dto.external.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BrandUpdateRequest", description = "브랜드 수정 요청 정보")
public record BrandUpdateRequest(
    String brandName
) {

}
