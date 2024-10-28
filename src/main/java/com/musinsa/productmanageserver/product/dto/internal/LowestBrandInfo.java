package com.musinsa.productmanageserver.product.dto.internal;

import com.musinsa.productmanageserver.common.util.MoneyUtil;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LowestBrandInfo {

    private String brandName;
    private List<CategoryPriceInfo> categoryPriceInfoList;
    private String totalPrice;

    @Builder(builderClassName = "OfBuilder", builderMethodName = "ofBuilder")
    public LowestBrandInfo(String brandName, List<CategoryPriceInfo> categoryPriceInfoList,
        int totalPrice) {
        this.brandName = brandName;
        this.categoryPriceInfoList = categoryPriceInfoList;
        this.totalPrice = MoneyUtil.convertStringFormat(totalPrice);
    }
}
