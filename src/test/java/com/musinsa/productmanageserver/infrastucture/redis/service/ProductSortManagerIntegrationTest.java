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
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RStream;
import org.redisson.api.StreamMessageId;
import org.redisson.api.stream.StreamCreateGroupArgs;
import org.redisson.api.stream.StreamReadGroupArgs;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductSortManagerIntegrationTest {

    @Autowired
    private ProductSortManager productSortManager;

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
        productInfoList.forEach(productSortManager::addToCategorySet);
        //productInfoList.forEach(productCacheServiceImpl::addToBrandCategorySet);
        System.out.println("==================================");
    }

    @Test
    void test() {
        productInfoList.forEach(productSortManager::addToCategorySet);
        productInfoList.forEach(productSortManager::addToBrandCategorySet);
        System.out.println("==================================");
    }

    @Test
    void test2() throws JsonProcessingException {

        RScoredSortedSet<String> set = redisCommandComponent.getScoredSortedSet("test");
        set.add(100, "1");
        set.add(200, "2");
        set.add(300, "1");

        System.out.println("=====");
    }

    @Test
    void test3() throws JsonProcessingException {
        productInfoList.forEach(productSortManager::addToCategorySet);
        productInfoList.forEach(productSortManager::addToBrandCategorySet);

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

            productSortManager.addToBrandSet(brandInfo.getBrandName(), lowestBrandPrice);
        });

        System.out.println("==================================");
    }
}