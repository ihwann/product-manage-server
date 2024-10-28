package com.musinsa.productmanageserver.product.dto.external.response;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BrandResponse", description = "브랜드 정보")
public record BrandResponse (
    Long brandId,
    String brandName
){

}
