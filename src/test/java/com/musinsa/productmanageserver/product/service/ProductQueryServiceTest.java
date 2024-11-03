package com.musinsa.productmanageserver.product.service;


import static com.musinsa.productmanageserver.mock.MockData.getBrandInfo;
import static com.musinsa.productmanageserver.mock.MockData.getProductInfo;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.exception.NotFoundResourceException;
import com.musinsa.productmanageserver.infrastucture.redis.service.ProductPriceComparisonManager;
import com.musinsa.productmanageserver.product.dto.internal.CategoryPriceInfo;
import com.musinsa.productmanageserver.product.dto.internal.LowestBrandInfo;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import com.musinsa.productmanageserver.product.repository.ProductRepository;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.redisson.client.protocol.ScoredEntry;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ProductQueryServiceTest {

    @InjectMocks
    private ProductQueryService productQueryService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private ProductPriceComparisonManager productPriceComparisonManager;

    @DisplayName("카테고리별 최저가 상품 조회 성공 테스트")
    @Test
    void getLowestPriceBrandsByCategory_Success() {
        // given
        for (Category category : Category.values()) {
            given(productPriceComparisonManager.getLowestPriceProductByCategory(category))
                .willReturn(Optional.of("123"));
        }

        ProductQueryService spy = spy(productQueryService);
        doReturn(Optional.of(new ProductInfo())).when(spy).findProductInfo(123L);

        // when
        List<ProductInfo> result = spy.getLowestPriceBrandsByCategory();

        // then
        assertEquals(Category.values().length, result.size());
    }

    @DisplayName("카테고리별 최저가 상품 조회 empty 테스트")
    @Test
    void getLowestPriceBrandsByCategory_SomeCategoriesHaveNoProducts() {
        // given
        given(productPriceComparisonManager.getLowestPriceProductByCategory(Category.TOP))
            .willReturn(Optional.of("123"));
        given(productPriceComparisonManager.getLowestPriceProductByCategory(Category.BOTTOM))
            .willReturn(Optional.empty());
        given(productPriceComparisonManager.getLowestPriceProductByCategory(Category.OUTER))
            .willReturn(Optional.of("456"));

        ProductQueryService spy = spy(productQueryService);
        doReturn(Optional.of(new ProductInfo())).when(spy).findProductInfo(123L);
        doReturn(Optional.of(new ProductInfo())).when(spy).findProductInfo(456L);

        // when
        List<ProductInfo> result = spy.getLowestPriceBrandsByCategory();

        // then
        assertEquals(2, result.size());
    }

    @DisplayName("카테고리별 최저가 상품 조회 실패 테스트")
    @Test
    void getLowestPriceBrandsByCategory_findProductInfoReturnsEmpty() {
        // given
        when(productPriceComparisonManager.getLowestPriceProductByCategory(any(Category.class)))
            .thenReturn(Optional.of("123"));

        ProductQueryService spy = spy(productQueryService);
        doReturn(Optional.empty()).when(spy).findProductInfo(123L);

        // when
        List<ProductInfo> result = spy.getLowestPriceBrandsByCategory();

        // then
        assertTrue(result.isEmpty());
    }

    @DisplayName("카테고리별 최저가 상품 조회 성공 테스트")
    @Test
    void getHighestPriceAllProductByCategory_ProductsAvailable() {
        // given
        Category category = Category.HAT;
        List<String> productIds = Arrays.asList("123", "456", "789");

        given(productPriceComparisonManager.getHighestPriceAllProductByCategory(any(Category.class)))
            .willReturn(productIds);

        ProductQueryService spy = spy(productQueryService);
        doReturn(Optional.of(new ProductInfo())).when(spy).findProductInfo(123L);
        doReturn(Optional.of(new ProductInfo())).when(spy).findProductInfo(456L);
        doReturn(Optional.of(new ProductInfo())).when(spy).findProductInfo(789L);

        // when
        List<ProductInfo> result = spy.getHighestPriceAllProductByCategory(category);

        // then
        assertEquals(3, result.size());
    }

    @DisplayName("카테고리별 최고가 상품 조회 db에 상품이 없는 경우 테스트")
    @Test
    void getHighestPriceAllProductByCategory_FindProductInfoReturnsEmpty() {
        // given
        Category category = Category.OUTER;
        List<String> productIds = Arrays.asList("123", "456", "789");

        given(productPriceComparisonManager.getHighestPriceAllProductByCategory(any(Category.class)))
            .willReturn(productIds);

        ProductQueryService spy = spy(productQueryService);
        doReturn(Optional.empty()).when(spy).findProductInfo(anyLong());

        // when
        List<ProductInfo> result = spy.getHighestPriceAllProductByCategory(category);

        // then
        assertTrue(result.isEmpty());
    }

    @DisplayName("카테고리별 최고가 상품 조회 set에 상품이 없는 경우 테스트")
    @Test
    void getHighestPriceAllProductByCategory_NoProductsAvailable() {
        // given
        Category category = Category.BOTTOM;

        given(productPriceComparisonManager.getHighestPriceAllProductByCategory(any(Category.class)))
            .willReturn(Collections.emptyList());

        ProductQueryService spy = spy(productQueryService);

        // when
        List<ProductInfo> result = spy.getHighestPriceAllProductByCategory(category);

        // then
        assertTrue(result.isEmpty());
        verify(spy, never()).findProductInfo(anyLong());
    }

    @DisplayName("카테고리별 최저가 상품 조회 성공 테스트")
    @Test
    void getLowestPriceAllProductByCategory_ProductsAvailable() {
        // given
        Category category = Category.TOP;
        List<String> productIds = Arrays.asList("123", "456", "789");

        given(productPriceComparisonManager.getLowestPriceAllProductByCategory(any(Category.class)))
            .willReturn(productIds);

        ProductQueryService spy = spy(productQueryService);
        doReturn(Optional.of(new ProductInfo())).when(spy).findProductInfo(123L);
        doReturn(Optional.of(new ProductInfo())).when(spy).findProductInfo(456L);
        doReturn(Optional.of(new ProductInfo())).when(spy).findProductInfo(789L);

        // when
        List<ProductInfo> result = spy.getLowestPriceAllProductByCategory(category);

        // then
        assertEquals(3, result.size());
    }

    @DisplayName("카테고리별 최저가 상품 조회 db에 상품이 없는 경우 테스트")
    @Test
    void getLowestPriceAllProductByCategory_FindProductInfoReturnsEmpty() {
        // given
        Category category = Category.TOP;
        List<String> productIds = Arrays.asList("123", "456", "789");

        given(productPriceComparisonManager.getLowestPriceAllProductByCategory(any(Category.class)))
            .willReturn(productIds);

        ProductQueryService spy = spy(productQueryService);
        doReturn(Optional.empty()).when(spy).findProductInfo(anyLong());

        // when
        List<ProductInfo> result = spy.getLowestPriceAllProductByCategory(category);

        // then
        assertTrue(result.isEmpty());
    }

    @DisplayName("카테고리별 최저가 상품 조회 set에 상품이 없는 경우 테스트")
    @Test
    void getLowestPriceAllProductByCategory_NoProductsAvailable() {
        // given
        Category category = Category.TOP;

        given(productPriceComparisonManager.getLowestPriceAllProductByCategory(any(Category.class)))
            .willReturn(Collections.emptyList());

        ProductQueryService spy = spy(productQueryService);

        // when
        List<ProductInfo> result = spy.getLowestPriceAllProductByCategory(category);

        // then
        assertTrue(result.isEmpty());
        verify(spy, never()).findProductInfo(anyLong());
    }

    @DisplayName("최저가 브랜드의 카테고리별 상품들 조회 성공 테스트")
    @Test
    void getLowestPriceBrand_Success() {
        // given
        String brandName = "BrandA";
        Double score = Category.values().length * 100.0;

        ScoredEntry<String> lowestPriceBrandEntry = new ScoredEntry<>(score, brandName);

        given(productPriceComparisonManager.getLowestPriceBrand()).willReturn(Optional.of(lowestPriceBrandEntry));

        for (Category category : Category.values()) {
            given(productPriceComparisonManager.getLowestPriceProductByBrandCategory(brandName, category))
                .willReturn(Optional.of(String.valueOf(category.ordinal() + 100))); // Product IDs: 100, 101, etc.
        }

        ProductQueryService spy = spy(productQueryService);

        for (Category category : Category.values()) {
            Long productId = (long) (category.ordinal() + 100);
            ProductInfo productInfo = getProductInfo(productId, getBrandInfo(), category,
                "Product" + category.name(), 100);
            doReturn(Optional.of(productInfo)).when(spy).findProductInfo(productId);
        }

        // when
        LowestBrandInfo result = spy.getLowestPriceBrand();

        // then
        assertNotNull(result);
        assertEquals(brandName, result.getBrandName());
        assertEquals("800", result.getTotalPrice());

        List<CategoryPriceInfo> categoryPriceInfoList = result.getCategoryPriceInfoList();
        assertEquals(Category.values().length, categoryPriceInfoList.size());

        for (CategoryPriceInfo categoryPriceInfo : categoryPriceInfoList) {
            assertNotNull(categoryPriceInfo.getCategory());
            assertNotNull(categoryPriceInfo.getPrice());
        }
    }

    @DisplayName("최저가 브랜드 조회 - NotFoundResourceException 발생 테스트")
    @Test
    void getLowestPriceBrand_NotFoundException() {
        // given
        given(productPriceComparisonManager.getLowestPriceBrand()).willReturn(Optional.empty());

        // when
        NotFoundResourceException exception = assertThrows(NotFoundResourceException.class, () -> {
            productQueryService.getLowestPriceBrand();
        });

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals("최저가 브랜드가 존재하지 않습니다.", exception.getMessage());
    }
}