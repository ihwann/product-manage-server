package com.musinsa.productmanageserver.infrastucture.redis.service;

import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import java.util.List;

public interface ProductCacheService {
    List<ProductInfo> sortedProductListByCategory(ProductInfo product);

    void getCheapestBrandsByCategory();
}
