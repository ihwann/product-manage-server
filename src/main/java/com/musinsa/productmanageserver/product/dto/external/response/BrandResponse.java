package com.musinsa.productmanageserver.product.dto.external.response;

import com.musinsa.productmanageserver.product.dto.internal.BrandInfo;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "BrandResponse", description = "브랜드 정보")
public record BrandResponse(
    Long brandId,
    String brandName
) {

    public static BrandResponse from(BrandInfo brandInfo) {
        return new BrandResponse(brandInfo.getBrandId(), brandInfo.getBrandName());
    }
}
