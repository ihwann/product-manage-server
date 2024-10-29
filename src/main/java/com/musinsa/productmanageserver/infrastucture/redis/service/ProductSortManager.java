package com.musinsa.productmanageserver.infrastucture.redis.service;

import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.common.util.RedisKeyGenerator;
import com.musinsa.productmanageserver.infrastucture.redis.component.RedisCommandComponent;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductSortManager {

    private final RedisCommandComponent redisCommandComponent;

    @Value("${redis.key.price-sorted-set.brand}")
    private String priceSortedSetBrandKey;

    @Value("${redis.key.price-sorted-set.category}")
    private String priceSortedSetCategoryKey;

    @Value("${redis.key.price-sorted-set.brand-category}")
    private String priceSortedSetBrandCategoryKey;

    /**
     * 카테고리별 최고가 모든 상품 조회
     * @param category 카테고리
     * @return 카테고리별 최고가 모든 상품id
     */
    public List<String> getHighestPriceAllProductByCategory(Category category) {

        String categoryKey = RedisKeyGenerator.generateKey(priceSortedSetCategoryKey,
            category.name());

        RScoredSortedSet<String> scoredSortedSet = getScoredSortedSet(categoryKey);
        Double lastScore = scoredSortedSet.lastScore();

        return scoredSortedSet.valueRange(lastScore, true, lastScore, true)
            .stream()
            .toList();
    }

    /**
     * 카테고리별 최저가 모든 상품 조회
     * @param category 카테고리
     * @return 카테고리별 최저가 모든 상품id
     */
    public List<String> getLowestPriceAllProductByCategory(Category category) {

        String categoryKey = RedisKeyGenerator.generateKey(priceSortedSetCategoryKey,
            category.name());

        RScoredSortedSet<String> scoredSortedSet = getScoredSortedSet(categoryKey);
        Double firstScore = scoredSortedSet.firstScore();

        return scoredSortedSet.valueRange(firstScore, true, firstScore, true)
            .stream()
            .toList();
    }


    /**
     * 카테고리별 최저가 상품 조회
     * @param category 카테고리
     * @return 카테고리별 최저가 상품id
     */
    public Optional<String> getLowestPriceProductByCategory(Category category) {

        String categoryKey = RedisKeyGenerator.generateKey(priceSortedSetCategoryKey,
            category.name());

        RScoredSortedSet<String> scoredSortedSet = getScoredSortedSet(categoryKey);

        return Optional.ofNullable(getScoredSortedSet(categoryKey))
            .filter(set -> !set.isEmpty())
            .map(RScoredSortedSet::first);
    }

    /**
     * 최저가 브랜드 조회
     * @return 최저가 브랜드
     */
    public Optional<ScoredEntry<String>> getLowestPriceBrand() {
        String brandKey = RedisKeyGenerator.generateKey(priceSortedSetBrandKey);

        RScoredSortedSet<String> scoredSortedSet = getScoredSortedSet(brandKey);

        return Optional.ofNullable(scoredSortedSet.firstEntry());
    }

    /**
     * 브랜드와 카테고리별 최저가 상품 조회
     * @param brandName 브랜드 이름
     * @param category 카테고리
     * @return 브랜드와 카테고리별 최저가 상품id
     */
    public Optional<String> getLowestPriceProductByBrandCategory(String brandName, Category category) {
        String brandCategoryKey = RedisKeyGenerator.generateKey(priceSortedSetBrandCategoryKey,
            brandName, category.name());

        RScoredSortedSet<String> scoredSortedSet = getScoredSortedSet(brandCategoryKey);

        return Optional.ofNullable(getScoredSortedSet(brandCategoryKey))
            .filter(set -> !set.isEmpty())
            .map(RScoredSortedSet::first);
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
            .add(productInfo.getProductPrice(), String.valueOf(productInfo.getProductId()));
    }


    /**
     * 브랜드-카테고리별 최저가 세트에 상품 추가
     * @return 추가된 상품의 브랜드-카테고리 랭크
     */
    public Integer addAndGetRankBrandCategorySet(ProductInfo productInfo) {
        String brandCategoryKey = RedisKeyGenerator.generateKey(priceSortedSetBrandCategoryKey,
            productInfo.getBrandInfo().getBrandName(), productInfo.getCategory().name());

        return getScoredSortedSet(brandCategoryKey)
            .addAndGetRank(productInfo.getProductPrice(), String.valueOf(productInfo.getProductId()));
    }

    /**
     * 브랜드별 최저가 세트에 상품 추가
     */
    public void addToBrandSet(String brandName, int brandPrice) {
        String brandKey = RedisKeyGenerator.generateKey(priceSortedSetBrandKey);

        getScoredSortedSet(brandKey).add(brandPrice, brandName);
    }

    /**
     * 브랜드별 최저가 세트 갱신
     * @param brandName 브랜드 이름
     */
    public void refreshBrandCategorySet(String brandName) {
        int totalPriceOfBrand = Arrays.stream(Category.values())
            .map(category -> getLowestPriceProductPriceByBrandCategory(brandName, category))
            .flatMap(Optional::stream)
            .mapToInt(Integer::intValue)
            .sum();

        String brandKey = RedisKeyGenerator.generateKey(priceSortedSetBrandKey);

        if (totalPriceOfBrand == 0) {
            getScoredSortedSet(brandKey).remove(brandName);
        } else {
            getScoredSortedSet(brandKey).add(totalPriceOfBrand, brandName);
        }
    }

    /**
     * 브랜드와 카테고리별 최저가 상품 조회
     * @param brandName 브랜드 이름
     * @param category 카테고리
     * @return 브랜드와 카테고리별 최저가 상품 가격
     */
    public Optional<Integer> getLowestPriceProductPriceByBrandCategory(String brandName,
        Category category) {
        String brandCategoryKey = RedisKeyGenerator.generateKey(priceSortedSetBrandCategoryKey,
            brandName, category.name());

        return Optional.ofNullable(getScoredSortedSet(brandCategoryKey))
            .filter(set -> !set.isEmpty())
            .map(set -> set.firstScore().intValue());
    }

    /**
     * 카테고리별 최저가 상품 삭제
     * @param productInfo 상품 정보
     */
    public void deleteFromCategorySet(ProductInfo productInfo) {
        String categoryKey = RedisKeyGenerator.generateKey(priceSortedSetCategoryKey,
            productInfo.getCategory().name());

        getScoredSortedSet(categoryKey).remove(String.valueOf(productInfo.getProductId()));
    }

    /**
     * 브랜드-카테고리별 최저가 상품 삭제
     * @param productInfo 상품 정보
     */
    public void deleteAndGetRankBrandCategorySet(ProductInfo productInfo) {
        String brandCategoryKey = RedisKeyGenerator.generateKey(priceSortedSetBrandCategoryKey,
            productInfo.getBrandInfo().getBrandName(), productInfo.getCategory().name());

        getScoredSortedSet(brandCategoryKey).remove(String.valueOf(productInfo.getProductId()));
    }
}
