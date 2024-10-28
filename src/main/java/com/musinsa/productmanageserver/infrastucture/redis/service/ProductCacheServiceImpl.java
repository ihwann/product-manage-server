package com.musinsa.productmanageserver.infrastucture.redis.service;

import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.common.util.DataParser;
import com.musinsa.productmanageserver.common.util.RedisKeyGenerator;
import com.musinsa.productmanageserver.infrastucture.redis.component.RedisCommandComponent;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductCacheServiceImpl{

    private final RedisCommandComponent redisCommandComponent;
    private final DataParser dataParser;

    @Value("${redis.key.price-sorted-set.brand}")
    private String priceSortedSetBrandKey;

    @Value("${redis.key.price-sorted-set.category}")
    private String priceSortedSetCategoryKey;

    @Value("${redis.key.price-sorted-set.brand-category}")
    private String priceSortedSetBrandCategoryKey;

    /**
     * 카테고리별 최고가 모든 상품 조회
     * @param category 카테고리
     * @return 카테고리별 최고가 모든 상품
     */
    public List<ProductInfo> getHighestPriceAllProductByCategory(Category category) {

        String categoryKey = RedisKeyGenerator.generateKey(priceSortedSetCategoryKey,
            category.name());

        RScoredSortedSet<String> scoredSortedSet = getScoredSortedSet(categoryKey);
        Double lastScore = scoredSortedSet.lastScore();

        return scoredSortedSet.valueRange(lastScore, true, lastScore, true)
            .stream()
            .map(this::parseToProductInfo)
            .toList();
    }

    /**
     * 카테고리별 최저가 모든 상품 조회
     * @param category 카테고리
     * @return 카테고리별 최저가 모든 상품
     */
    public List<ProductInfo> getLowestPriceAllProductByCategory(Category category) {

        String categoryKey = RedisKeyGenerator.generateKey(priceSortedSetCategoryKey,
            category.name());

        RScoredSortedSet<String> scoredSortedSet = getScoredSortedSet(categoryKey);
        Double firstScore = scoredSortedSet.firstScore();

        return scoredSortedSet.valueRange(firstScore, true, firstScore, true)
            .stream()
            .map(this::parseToProductInfo)
            .toList();
    }


    /**
     * 카테고리별 최저가 상품 조회
     * @param category 카테고리
     * @return 카테고리별 최저가 상품
     */
    public ProductInfo getLowestPriceProductByCategory(Category category) {

        String categoryKey = RedisKeyGenerator.generateKey(priceSortedSetCategoryKey,
            category.name());

        RScoredSortedSet scoredSortedSet = getScoredSortedSet(categoryKey);

        return parseToProductInfo(scoredSortedSet.first());
    }


    /**
     * 카테고리별 최저가 세트 조회
     * @param categoryKey 카테고리별 최저가 세트 키
     * @return 카테고리별 최저가 세트
     */
    private RScoredSortedSet<String> getScoredSortedSet(String categoryKey) {

        return redisCommandComponent.getScoredSortedSet(categoryKey);
    }

    /**
     * 카테고리별 최저가 세트에 상품 추가
     */
    public void addToCategorySet(ProductInfo productInfo) {
        String categoryKey = RedisKeyGenerator.generateKey(priceSortedSetCategoryKey,
            productInfo.getCategory().name());

        getScoredSortedSet(categoryKey)
            .add(productInfo.getProductPrice(), dataParser.parseToString(productInfo));
    }


    /**
     * 브랜드-카테고리별 최저가 세트에 상품 추가
     */
    public void addToBrandCategorySet(ProductInfo productInfo) {
        String brandCategoryKey = RedisKeyGenerator.generateKey(priceSortedSetBrandCategoryKey,
            productInfo.getBrandInfo().getBrandName(), productInfo.getCategory().name());

        getScoredSortedSet(brandCategoryKey)
            .add(productInfo.getProductPrice(), dataParser.parseToString(productInfo));
    }

    /**
     * 브랜드별 최저가 세트에 상품 추가
     */
    public void addToBrandSet(String brandName, int brandPrice) {
        String brandKey = RedisKeyGenerator.generateKey(priceSortedSetBrandKey);

        getScoredSortedSet(brandKey).add(brandPrice, brandName);
    }

    /**
     * 상품 정보로 변환
     * @param item 상품 정보
     * @return 상품 정보
     */
    private ProductInfo parseToProductInfo(Object item) {
        return dataParser.parse(item.toString(), ProductInfo.class);
    }
}
