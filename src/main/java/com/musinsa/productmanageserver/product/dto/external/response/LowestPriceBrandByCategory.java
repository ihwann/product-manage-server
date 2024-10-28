package com.musinsa.productmanageserver.product.dto.external.response;

import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.common.util.MoneyUtil;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LowestPriceBrandByCategory {

    private Category category; // 카테고리
    private String brandName; // 가장 저렴한 브랜드 이름
    private String price; // 가장 저렴한 상품 가격

    @Builder(builderClassName = "FromProductInfoBuilder", builderMethodName = "fromProductInfoBuilder")
    public LowestPriceBrandByCategory(ProductInfo productInfo) {
        this.category = productInfo.getCategory();
        this.brandName = productInfo.getBrandInfo().getBrandName();
        this.price = MoneyUtil.convertStringFormat(productInfo.getProductPrice());
    }
}
