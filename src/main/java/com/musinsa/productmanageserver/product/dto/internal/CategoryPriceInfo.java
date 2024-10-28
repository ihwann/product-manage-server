package com.musinsa.productmanageserver.product.dto.internal;

import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.common.util.MoneyUtil;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryPriceInfo {

    private Category category;
    private String price;

    @Builder(builderClassName = "FromProductInfoBuilder", builderMethodName = "fromProductInfoBuilder")
    public CategoryPriceInfo(ProductInfo productInfo) {
        this.category = productInfo.getCategory();
        this.price = MoneyUtil.convertStringFormat(productInfo.getProductPrice());
    }
}
