package com.musinsa.productmanageserver.infrastucture.redis.service;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.common.util.RedisKeyGenerator;
import com.musinsa.productmanageserver.infrastucture.redis.component.RedisCommandComponent;
import com.musinsa.productmanageserver.infrastucture.redis.config.RedisConfigTest;
import com.musinsa.productmanageserver.mock.MockData;
import com.musinsa.productmanageserver.product.dto.internal.BrandInfo;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.redisson.api.RScoredSortedSet;
import org.redisson.api.RedissonClient;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;

@Import(RedisConfigTest.class)
@ExtendWith({SpringExtension.class})
class ProductPriceComparisonManagerTest {

    @InjectMocks
    private ProductPriceComparisonManager productPriceComparisonManager;

    @Mock
    private RedisCommandComponent redisCommandComponent;

    @Autowired
    private RedissonClient redissonClient;

    @BeforeEach
    void setUp() {
        redisCommandComponent = new RedisCommandComponent(redissonClient);
        productPriceComparisonManager = new ProductPriceComparisonManager(redisCommandComponent);
        ReflectionTestUtils.setField(productPriceComparisonManager, "priceSortedSetBrandKey",
            "price-sorted-set-by-brand");
        ReflectionTestUtils.setField(productPriceComparisonManager, "priceSortedSetCategoryKey",
            "price-sorted-set-by-category");
        ReflectionTestUtils.setField(productPriceComparisonManager, "priceSortedSetBrandCategoryKey",
            "price-sorted-set-by-brand-category");
    }

    @AfterEach
    void tearDown() {
        redissonClient.getKeys().deleteByPattern("*");
    }

    @DisplayName("카테고리별 최고가 모든 상품 조회 - set에 데이터가 없는 경우")
    @Test
    void getHighestPriceAllProductByCategory_returnEmptyList() {

        //given, when
        Arrays.stream(Category.values())
            .forEach(category -> {
                List<String> productIdList = productPriceComparisonManager.getHighestPriceAllProductByCategory(
                    category);

                //then
                assertTrue(productIdList.isEmpty());
            });
    }

    @DisplayName("카테고리별 최고가 모든 상품 조회 - set에 데이터가 있는 경우")
    @Test
    void getHighestPriceAllProductByCategory_returnProductList() {
        //given
        productPriceComparisonManager.addToCategorySet(
            MockData.getProductInfo(1L, null, Category.SNEAKERS, null, 1000));
        productPriceComparisonManager.addToCategorySet(
            MockData.getProductInfo(2L, null, Category.SNEAKERS, null, 1000));
        productPriceComparisonManager.addToCategorySet(
            MockData.getProductInfo(3L, null, Category.SNEAKERS, null, 900));

        //when
        List<String> productIdList = productPriceComparisonManager.getHighestPriceAllProductByCategory(
            Category.SNEAKERS);

        //then
        assertAll(
            () -> assertTrue(productIdList.contains("1")),
            () -> assertTrue(productIdList.contains("2")),
            () -> assertFalse(productIdList.contains("3"))
        );
    }

    @DisplayName("카테고리별 최저가 모든 상품 조회 - set에 데이터가 없는 경우")
    @Test
    void getLowestPriceAllProductByCategory_returnEmptyList() {

        //given, when
        Arrays.stream(Category.values())
            .forEach(category -> {
                List<String> productIdList = productPriceComparisonManager.getLowestPriceAllProductByCategory(
                    category);

                //then
                assertTrue(productIdList.isEmpty());
            });
    }

    @DisplayName("카테고리별 최저가 모든 상품 조회 - set에 데이터가 있는 경우")
    @Test
    void getLowestPriceAllProductByCategory_returnProductList() {
        //given
        productPriceComparisonManager.addToCategorySet(
            MockData.getProductInfo(1L, null, Category.SNEAKERS, null, 1000));
        productPriceComparisonManager.addToCategorySet(
            MockData.getProductInfo(2L, null, Category.SNEAKERS, null, 1000));
        productPriceComparisonManager.addToCategorySet(
            MockData.getProductInfo(3L, null, Category.SNEAKERS, null, 900));

        //when
        List<String> productIdList = productPriceComparisonManager.getLowestPriceAllProductByCategory(
            Category.SNEAKERS);

        //then
        assertAll(
            () -> assertFalse(productIdList.contains("1")),
            () -> assertFalse(productIdList.contains("2")),
            () -> assertTrue(productIdList.contains("3"))
        );
    }

    @DisplayName("카테고리별 최저가 상품 조회 - set에 데이터가 있는 경우")
    @Test
    void getLowestPriceProductByCategory_returnProduct() {
        //given
        productPriceComparisonManager.addToCategorySet(
            MockData.getProductInfo(1L, null, Category.SNEAKERS, null, 1000));
        productPriceComparisonManager.addToCategorySet(
            MockData.getProductInfo(2L, null, Category.SNEAKERS, null, 1000));
        productPriceComparisonManager.addToCategorySet(
            MockData.getProductInfo(3L, null, Category.SNEAKERS, null, 900));

        //when
        String productId = productPriceComparisonManager.getLowestPriceProductByCategory(Category.SNEAKERS)
            .orElse(null);

        //then
        assertEquals("3", productId);
    }

    @DisplayName("카테고리별 최저가 상품 조회 - set에 데이터가 없는 경우")
    @Test
    void getLowestPriceProductByCategory_returnEmpty() {
        //given, when
        Optional<String> productId = productPriceComparisonManager.getLowestPriceProductByCategory(
            Category.SNEAKERS);

        //then
        assertTrue(productId.isEmpty());
    }

    @DisplayName("최저가 브랜드 조회 - set에 데이터가 없는 경우")
    @Test
    void getLowestPriceBrand_returnEmpty() {
        //given, when
        Optional<ScoredEntry<String>> lowestPriceBrand = productPriceComparisonManager.getLowestPriceBrand();

        //then
        assertTrue(lowestPriceBrand.isEmpty());
    }

    @DisplayName("브랜드와 카테고리별 최저가 상품 조회 - set에 데이터가 있는 경우")
    @Test
    void getLowestPriceProductByBrandCategory_returnProduct() {
        //given
        BrandInfo brandInfo = MockData.getBrandInfo();
        productPriceComparisonManager.addAndGetRankBrandCategorySet(MockData.getProductInfo(1L, brandInfo,
            Category.TOP, null, 3000));
        productPriceComparisonManager.addAndGetRankBrandCategorySet(MockData.getProductInfo(2L, brandInfo,
            Category.TOP, null, 2000));
        productPriceComparisonManager.addAndGetRankBrandCategorySet(MockData.getProductInfo(3L, brandInfo,
            Category.TOP, null, 1000));

        //when
        String productId = productPriceComparisonManager.getLowestPriceProductByBrandCategory(
            brandInfo.getBrandName(), Category.TOP).orElse(null);

        //then
        assertEquals("3", productId);
    }

    @DisplayName("브랜드와 카테고리별 최저가 상품 조회 - set에 데이터가 없는 경우")
    @Test
    void getLowestPriceProductByBrandCategory_returnEmpty() {
        //given, when
        BrandInfo brandInfo = MockData.getBrandInfo();

        Optional<String> productId = productPriceComparisonManager.getLowestPriceProductByBrandCategory(
            brandInfo.getBrandName(), Category.TOP);

        //then
        assertTrue(productId.isEmpty());
    }

    @DisplayName("카테고리별 최저가 세트에 상품 추가 성공 테스트")
    @Test
    void addToCategorySet_returnSuccess() {
        //given
        BrandInfo brandInfo = MockData.getBrandInfo();

        //when
        productPriceComparisonManager.addToCategorySet(
            MockData.getProductInfo(1L, brandInfo, Category.TOP, null, 1000));

        List<String> productIdList = productPriceComparisonManager.getLowestPriceAllProductByCategory(
            Category.TOP);

        //then
        assertTrue(productIdList.contains("1"));
    }

    @DisplayName("브랜드-카테고리별 최저가 세트에 상품 추가 성공 테스트")
    @Test
    void addAndGetRankBrandCategorySet_returnSuccess() {
        //given
        BrandInfo brandInfo = MockData.getBrandInfo();

        //when
        productPriceComparisonManager.addAndGetRankBrandCategorySet(
            MockData.getProductInfo(1L, brandInfo, Category.TOP, null, 2000));
        productPriceComparisonManager.addAndGetRankBrandCategorySet(
            MockData.getProductInfo(2L, brandInfo, Category.TOP, null, 1000));

        String productId = productPriceComparisonManager.getLowestPriceProductByBrandCategory(
            brandInfo.getBrandName(), Category.TOP).orElse(null);

        //then
        assertEquals("2", productId);
    }

    @DisplayName("브랜드별 최저가 세트 갱신 성공 테스트")
    @Test
    void updateLowestPriceBrand_returnSuccess() {
        //given
        BrandInfo brandInfo = MockData.getBrandInfo();

        productPriceComparisonManager.addAndGetRankBrandCategorySet(
            MockData.getProductInfo(1L, brandInfo, Category.TOP, null, 2000));
        productPriceComparisonManager.addAndGetRankBrandCategorySet(
            MockData.getProductInfo(2L, brandInfo, Category.TOP, null, 1000));

        //when
        productPriceComparisonManager.refreshBrandCategorySet(brandInfo.getBrandName());

        //then
        Optional<ScoredEntry<String>> lowestPriceBrand = productPriceComparisonManager.getLowestPriceBrand();
        assertTrue(lowestPriceBrand.isPresent());
        assertEquals(1000, lowestPriceBrand.get().getScore().intValue());
    }

    @DisplayName("브랜드와 카테고리별 최저가 상품 조회 성공 테스트")
    @Test
    void getLowestPriceProductByBrandCategory_returnSuccess() {
        //given
        BrandInfo brandInfo = MockData.getBrandInfo();

        productPriceComparisonManager.addAndGetRankBrandCategorySet(
            MockData.getProductInfo(1L, brandInfo, Category.TOP, null, 1000));
        productPriceComparisonManager.addAndGetRankBrandCategorySet(
            MockData.getProductInfo(2L, brandInfo, Category.TOP, null, 2000));

        //when
        Optional<Integer> lowestPrice = productPriceComparisonManager.getLowestPriceProductPriceByBrandCategory(
            brandInfo.getBrandName(), Category.TOP);

        //then
        assertTrue(lowestPrice.isPresent());
        assertEquals(1000, lowestPrice.get());
    }

    @DisplayName("카테고리별 최저가 상품 삭제 성공 테스트")
    @Test
    void deleteFromCategorySet_returnSuccess() {
        //given
        BrandInfo brandInfo = MockData.getBrandInfo();

        productPriceComparisonManager.addToCategorySet(
            MockData.getProductInfo(1L, brandInfo, Category.TOP, null, 1000));
        productPriceComparisonManager.addToCategorySet(
            MockData.getProductInfo(2L, brandInfo, Category.TOP, null, 2000));

        //when
        productPriceComparisonManager.deleteFromCategorySet(
            MockData.getProductInfo(1L, brandInfo, Category.TOP, null, 1000));

        //then
        Optional<String> productId = productPriceComparisonManager.getLowestPriceProductByCategory(Category.TOP);

        assertTrue(productId.isPresent());
        assertEquals("2", productId.get());
    }

    @DisplayName("브랜드-카테고리별 최저가 상품 삭제 성공 테스트")
    @Test
    void deleteFromBrandCategorySet_returnSuccess() {
        //given
        BrandInfo brandInfo = MockData.getBrandInfo();

        productPriceComparisonManager.addAndGetRankBrandCategorySet(
            MockData.getProductInfo(1L, brandInfo, Category.TOP, null, 1000));
        productPriceComparisonManager.addAndGetRankBrandCategorySet(
            MockData.getProductInfo(2L, brandInfo, Category.TOP, null, 2000));

        //when
        productPriceComparisonManager.deleteAndGetRankBrandCategorySet(
            MockData.getProductInfo(1L, brandInfo, Category.TOP, null, 1000));

        //then
        Optional<String> productId = productPriceComparisonManager.getLowestPriceProductByBrandCategory(
            brandInfo.getBrandName(), Category.TOP);

        assertTrue(productId.isPresent());
        assertEquals("2", productId.get());
    }

    @DisplayName("브랜드 수정 테스트")
    @Test
    void updateBrandName_returnSuccess() {

        // given
        String oldBrandName = "oldBrandName";
        String newBrandName = "newBrandName";
        String brandKey = RedisKeyGenerator.generateKey("price-sorted-set-by-brand");

        RScoredSortedSet<String> brandSortedSet = redisCommandComponent.getScoredSortedSet(brandKey);

        // when
        brandSortedSet.add(1000, oldBrandName);
        brandSortedSet.replace(oldBrandName, newBrandName);

        // then
        RScoredSortedSet<String> newBrandSortedSet = redisCommandComponent.getScoredSortedSet(
            RedisKeyGenerator.generateKey("price-sorted-set-by-brand"));

        assertEquals(1, newBrandSortedSet.size());
        assertEquals(newBrandName, newBrandSortedSet.first());
        assertEquals(1000, newBrandSortedSet.firstScore().intValue());
    }

}