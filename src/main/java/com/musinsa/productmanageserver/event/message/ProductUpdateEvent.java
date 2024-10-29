package com.musinsa.productmanageserver.event.message;

import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;

public record ProductUpdateEvent(
    ProductInfo productInfo
) {

}
