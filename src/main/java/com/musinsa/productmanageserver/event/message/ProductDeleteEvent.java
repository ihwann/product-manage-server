package com.musinsa.productmanageserver.event.message;

import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;

public record ProductDeleteEvent(
    ProductInfo productInfo
) {

}