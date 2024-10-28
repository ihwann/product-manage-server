package com.musinsa.productmanageserver.product.dto.external.response;

import com.musinsa.productmanageserver.common.type.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

@Schema(name = "LowestAndHighestPriceProduct", description = "카테고리별 최저가격 상품과 최고가격 브랜드 조회 응답 정보")
public record LowestAndHighestPriceProduct(
    Category category,
    List<BrandPriceResponse> lowestPriceBrandList,
    List<BrandPriceResponse> highestPriceBrandList
) {

}
