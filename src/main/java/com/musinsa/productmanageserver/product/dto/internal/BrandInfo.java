package com.musinsa.productmanageserver.product.dto.internal;

import com.musinsa.productmanageserver.product.model.BrandEntity;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class BrandInfo {

    private Long brandId;   // 브랜드 ID
    private String brandName;  // 브랜드 이름

    @Builder(builderClassName = "FromEntityBuilder", builderMethodName = "fromEntityBuilder")
    public BrandInfo(BrandEntity entity) {
        this.brandId = entity.getId();
        this.brandName = entity.getBrandName();
    }

    @Builder
    public BrandInfo(Long brandId, String brandName) {
        this.brandId = brandId;
        this.brandName = brandName;
    }
}
