package com.musinsa.productmanageserver.product.dto.external.response;

import com.musinsa.productmanageserver.product.dto.internal.LowestBrandInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "LowestPriceBrandResponse", description = "최저가격 브랜드 응답")
public record LowestPriceBrandResponse(
    LowestBrandInfo lowestBrandInfo
) {

}
