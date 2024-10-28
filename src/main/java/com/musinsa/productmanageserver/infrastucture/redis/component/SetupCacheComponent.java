package com.musinsa.productmanageserver.infrastucture.redis.component;

import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.common.util.RedisKeyGenerator;
import com.musinsa.productmanageserver.infrastucture.redis.service.ProductSortManager;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import com.musinsa.productmanageserver.product.repository.BrandRepository;
import com.musinsa.productmanageserver.product.repository.ProductRepository;
import jakarta.annotation.PostConstruct;
import java.util.Arrays;
import lombok.RequiredArgsConstructor;
import org.redisson.api.RScoredSortedSet;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class SetupCacheComponent {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;
    private final ProductSortManager productCacheService;
    private final RedisCommandComponent redisCommandComponent;

    @PostConstruct
    @Transactional(readOnly = true)
    public void setup() {
        productRepository.findAllWithBrand()
            .stream()
            .map(entity -> ProductInfo.fromEntityBuilder()
                .entity(entity)
                .build())
            .forEach(productInfo -> {
                productCacheService.addToCategorySet(productInfo);
                productCacheService.addToBrandCategorySet(productInfo);
            });

        brandRepository.findAll()
            .forEach(brandInfo -> {
                int lowestBrandPrice = Arrays.stream(Category.values())
                    .map(category -> RedisKeyGenerator.generateKey(
                        "price-sorted-set-by-brand-category",
                        brandInfo.getBrandName(),
                        category.name()))
                    .map(redisCommandComponent::getScoredSortedSet)
                    .map(RScoredSortedSet::firstScore)
                    .mapToInt(Double::intValue)
                    .sum();

                productCacheService.addToBrandSet(brandInfo.getBrandName(), lowestBrandPrice);
            });
    }
}
