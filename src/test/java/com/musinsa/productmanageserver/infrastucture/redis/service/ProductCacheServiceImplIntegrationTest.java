package com.musinsa.productmanageserver.infrastucture.redis.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.common.util.RedisKeyGenerator;
import com.musinsa.productmanageserver.infrastucture.redis.component.RedisCommandComponent;
import com.musinsa.productmanageserver.product.dto.internal.BrandInfo;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import com.musinsa.productmanageserver.product.repository.BrandRepository;
import com.musinsa.productmanageserver.product.repository.ProductRepository;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductCacheServiceImplIntegrationTest {

    @Autowired
    private ProductCacheServiceImpl productCacheServiceImpl;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Autowired
    private RedisCommandComponent redisCommandComponent;

    @Autowired
    private ObjectMapper objectMapper;

    private List<ProductInfo> productInfoList;
    private List<BrandInfo> brandInfoList;

    @BeforeEach
    void setUp() {
        brandInfoList = brandRepository.findAll()
            .stream()
            .map(entity -> BrandInfo.fromEntityBuilder()
                .entity(entity)
                .build())
            .toList();
        productInfoList = productRepository.findAllWithBrand()
            .stream()
            .map(entity -> ProductInfo.fromEntityBuilder()
                .entity(entity)
                .build())
            .toList();
    }

    @Test
    void test0() {
        productInfoList.forEach(productCacheServiceImpl::addToCategorySet);
        //productInfoList.forEach(productCacheServiceImpl::addToBrandCategorySet);
        System.out.println("==================================");
    }

    @Test
    void test() {
        productInfoList.forEach(productCacheServiceImpl::addToCategorySet);
        productInfoList.forEach(productCacheServiceImpl::addToBrandCategorySet);
        System.out.println("==================================");
    }

    @Test
    void test2() throws JsonProcessingException {
        ProductInfo product = ProductInfo.builder()
            .productId(1L)
            .build();
        ProductInfo product2 = ProductInfo.builder()
            .productId(2L)
            .build();
        ProductInfo product3 = ProductInfo.builder()
            .productId(3L)
            .build();

        redisCommandComponent.getScoredSortedSet("test")
            .add(1, objectMapper.writeValueAsString(product));
        redisCommandComponent.getScoredSortedSet("test")
            .add(2, objectMapper.writeValueAsString(product));

        Object test = redisCommandComponent.getScoredSortedSet("test").first();
        Long productId = objectMapper.readValue(test.toString(), ProductInfo.class).getProductId();

        System.out.println("==================================");
    }

    @Test
    void test3() throws JsonProcessingException {
        productInfoList.forEach(productCacheServiceImpl::addToCategorySet);
        productInfoList.forEach(productCacheServiceImpl::addToBrandCategorySet);

        brandInfoList.forEach(brandInfo -> {
            int lowestBrandPrice = Arrays.stream(Category.values())
                .map(category -> RedisKeyGenerator.generateKey(
                    "price-sorted-set-by-brand-category",
                    brandInfo.getBrandName(),
                    category.name()))
                .map(redisCommandComponent::getScoredSortedSet)
                .map(RScoredSortedSet::firstScore)
                .mapToInt(Double::intValue)
                .sum();

            productCacheServiceImpl.addToBrandSet(brandInfo.getBrandName(), lowestBrandPrice);
        });

        System.out.println("==================================");
    }
}