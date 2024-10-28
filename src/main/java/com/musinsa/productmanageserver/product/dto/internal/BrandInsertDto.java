package com.musinsa.productmanageserver.product.dto.internal;

import com.musinsa.productmanageserver.product.dto.external.request.BrandInsertRequest;
import jakarta.validation.constraints.NotEmpty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BrandInsertDto {

    @NotEmpty
    private String brandName;

    @Builder(builderClassName = "FromRequestBuilder", builderMethodName = "fromRequestBuilder")
    public BrandInsertDto(BrandInsertRequest insertRequest) {
        this.brandName = insertRequest.brandName();
    }
}
