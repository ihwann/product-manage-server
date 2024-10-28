package com.musinsa.productmanageserver.product.service;

import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.infrastucture.redis.service.ProductCacheServiceImpl;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
 * 상품 조회 서비스
 */
@Service
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductCacheServiceImpl productCacheService;

    /**
     * 카테고리별 최저가 상품 조회
     *
     * @return 카테고리별 최저가 상품 리스트
     */
    public List<ProductInfo> getLowestPriceBrandsByCategory() {
        List<ProductInfo> lowestPriceBrands = new ArrayList<>();

        for (Category category : Category.values()) {
            ProductInfo productInfo = productCacheService.getLowestPriceProductByCategory(category);
            lowestPriceBrands.add(productInfo);
        }

        return lowestPriceBrands;
    }


    /**
     * 카테고리별 최고가 상품 조회
     * @param category 카테고리
     * @return 카테고리별 최고가 상품 리스트
     */
    public List<ProductInfo> getHighestPriceAllProductByCategory(Category category) {
        return productCacheService.getHighestPriceAllProductByCategory(category);
    }

    /**
     * 카테고리별 최저가 상품 조회
     * @param category 카테고리
     * @return 카테고리별 최저가 상품 리스트
     */
    public List<ProductInfo> getLowestPriceAllProductByCategory(Category category) {
        return productCacheService.getLowestPriceAllProductByCategory(category);
    }
}
