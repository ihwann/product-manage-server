package com.musinsa.productmanageserver.product.service;

import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.exception.NotFoundResourceException;
import com.musinsa.productmanageserver.infrastucture.redis.service.ProductPriceComparisonManager;
import com.musinsa.productmanageserver.product.dto.internal.CategoryPriceInfo;
import com.musinsa.productmanageserver.product.dto.internal.LowestBrandInfo;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import com.musinsa.productmanageserver.product.repository.ProductRepository;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

/**
 * 상품 조회 서비스
 */
@Service
@RequiredArgsConstructor
public class ProductQueryService {

    private final ProductPriceComparisonManager productPriceComparisonManager;
    private final ProductRepository productRepository;

    /**
     * 카테고리별 최저가 상품 조회
     *
     * @return 카테고리별 최저가 상품 리스트
     */
    public List<ProductInfo> getLowestPriceBrandsByCategory() {

        return Arrays.stream(Category.values())
            .map(productPriceComparisonManager::getLowestPriceProductByCategory)
            .flatMap(Optional::stream)
            .map(productId -> findProductInfo(Long.parseLong(productId)))
            .flatMap(Optional::stream)
            .toList();
    }


    /**
     * 카테고리별 최고가 상품 조회
     *
     * @param category 카테고리
     * @return 카테고리별 최고가 상품 리스트
     */
    public List<ProductInfo> getHighestPriceAllProductByCategory(Category category) {

        return productPriceComparisonManager.getHighestPriceAllProductByCategory(category)
            .stream()
            .map(productId -> findProductInfo(Long.parseLong(productId)))
            .flatMap(Optional::stream)
            .toList();
    }

    /**
     * 카테고리별 최저가 상품 조회
     *
     * @param category 카테고리
     * @return 카테고리별 최저가 상품 리스트
     */
    public List<ProductInfo> getLowestPriceAllProductByCategory(Category category) {

        return productPriceComparisonManager.getLowestPriceAllProductByCategory(category)
            .stream()
            .map(productId -> findProductInfo(Long.parseLong(productId)))
            .flatMap(Optional::stream)
            .toList();
    }

    /**
     * 최저가 브랜드 정보 조회
     *
     * @return 최저가 브랜드 정보
     */
    public LowestBrandInfo getLowestPriceBrand() {
        ScoredEntry<String> lowestPriceBrandEntry = productPriceComparisonManager.getLowestPriceBrand()
            .orElseThrow(
                () -> new NotFoundResourceException(HttpStatus.NOT_FOUND, "최저가 브랜드가 존재하지 않습니다."));

        List<CategoryPriceInfo> categoryPriceInfoList = getCategoryPriceListByBrandName(
            lowestPriceBrandEntry.getValue());

        return LowestBrandInfo.ofBuilder()
            .brandName(lowestPriceBrandEntry.getValue())
            .categoryPriceInfoList(categoryPriceInfoList)
            .totalPrice(lowestPriceBrandEntry.getScore().intValue())
            .build();
    }

    /**
     * 브랜드 이름으로 카테고리별 최저가 상품가격 조회
     * @param brandName 브랜드 이름
     * @return 카테고리별 최저가 상품가격 리스트
     */
    private List<CategoryPriceInfo> getCategoryPriceListByBrandName(String brandName) {

        return Arrays.stream(Category.values())
            .map(category -> productPriceComparisonManager.getLowestPriceProductByBrandCategory(
                brandName, category))
            .flatMap(Optional::stream)
            .map(productId -> findProductInfo(Long.parseLong(productId)))
            .flatMap(Optional::stream)
            .map(productInfo -> CategoryPriceInfo.fromProductInfoBuilder()
                .productInfo(productInfo)
                .build())
            .toList();
    }

    /**
     * 상품 정보 조회
     * @param productId 상품 ID
     * @return 상품 정보
     */
    public Optional<ProductInfo> findProductInfo(Long productId) {

        return productRepository.findByIdWithBrand(productId)
            .map(entity -> ProductInfo.fromEntityBuilder()
                .entity(entity)
                .build());
    }
}
