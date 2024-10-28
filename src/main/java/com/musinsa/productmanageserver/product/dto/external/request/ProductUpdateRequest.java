package com.musinsa.productmanageserver.product.dto.external.request;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProductUpdateRequest", description = "상품 수정 요청 정보")
public record ProductUpdateRequest(
    Integer price
) {

}
