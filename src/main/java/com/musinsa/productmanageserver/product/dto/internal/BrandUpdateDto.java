package com.musinsa.productmanageserver.product.dto.internal;

import com.musinsa.productmanageserver.product.dto.external.request.BrandUpdateRequest;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BrandUpdateDto {

    private Long brandId;
    private String brandName;

    @Builder(builderClassName = "FromRequestBuilder", builderMethodName = "fromRequestBuilder")
    public BrandUpdateDto(Long brandId, BrandUpdateRequest updateRequest) {
        this.brandId = brandId;
        this.brandName = updateRequest.brandName();
    }

    @Builder
    public BrandUpdateDto(Long brandId, String brandName) {
        this.brandId = brandId;
        this.brandName = brandName;
    }
}
