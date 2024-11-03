package com.musinsa.productmanageserver.product.service;

import static com.musinsa.productmanageserver.common.constant.Constants.ErrorMessage.NOT_FOUND_BRAND;
import static com.musinsa.productmanageserver.common.constant.Constants.ErrorMessage.NOT_FOUND_PRODUCT;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.musinsa.productmanageserver.event.component.ProductEventPublisher;
import com.musinsa.productmanageserver.exception.NotFoundResourceException;
import com.musinsa.productmanageserver.mock.MockData;
import com.musinsa.productmanageserver.product.dto.internal.BrandInfo;
import com.musinsa.productmanageserver.product.dto.internal.BrandInsertDto;
import com.musinsa.productmanageserver.product.dto.internal.BrandUpdateDto;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import com.musinsa.productmanageserver.product.dto.internal.ProductInsertDto;
import com.musinsa.productmanageserver.product.dto.internal.ProductUpdateDto;
import com.musinsa.productmanageserver.product.model.BrandEntity;
import com.musinsa.productmanageserver.product.model.ProductEntity;
import com.musinsa.productmanageserver.product.repository.BrandRepository;
import com.musinsa.productmanageserver.product.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;

@ExtendWith(MockitoExtension.class)
class ProductCommandServiceTest {

    @InjectMocks
    private ProductCommandService productCommandService;

    @Mock
    private ProductEventPublisher productEventPublisher;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private BrandRepository brandRepository;

    @DisplayName("상품 수정 성공 테스트")
    @Test
    void updateProduct_returnSuccess() {
        // given
        ProductUpdateDto updateDto = MockData.getProductUpdateDto();

        ProductEntity existingProduct = MockData.getProductEntity();

        given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));
        given(productRepository.save(existingProduct)).willReturn(existingProduct);

        // when
        ProductInfo result = productCommandService.updateProduct(updateDto);

        // then
        assertNotNull(result);
        assertEquals(20000, result.getProductPrice());

        verify(productRepository, times(1)).save(existingProduct);
        verify(productEventPublisher, times(1)).publishUpdateProductEvent(any(ProductInfo.class));

    }

    @DisplayName("상품 수정 - 상품이 없는 경우 테스트")
    @Test
    void updateProduct_ProductNotFound() {
        // given
        ProductUpdateDto updateDto = MockData.getProductUpdateDto();

        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        NotFoundResourceException exception = assertThrows(NotFoundResourceException.class, () -> {
            productCommandService.updateProduct(updateDto);
        });

        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals("상품이 존재하지 않습니다.", exception.getMessage());

        // Verify that no save or event publishing occurred
        verify(productRepository, never()).save(any(ProductEntity.class));
        verify(productEventPublisher, never()).publishUpdateProductEvent(any(ProductInfo.class));
    }

    @DisplayName("상품 삭제 성공 테스트")
    @Test
    void deleteProduct_returnSuccess() {
        // given
        ProductEntity existingProduct = MockData.getProductEntity();

        given(productRepository.findById(anyLong())).willReturn(Optional.of(existingProduct));

        // when
        productCommandService.deleteProduct(1L);

        // then
        verify(productRepository, times(1)).delete(existingProduct);
        verify(productEventPublisher, times(1)).publishDeleteProductEvent(any(ProductInfo.class));
    }

    @DisplayName("상품 삭제 - 상품이 없는 경우 테스트")
    @Test
    void deleteProduct_ProductNotFound() {
        // given
        when(productRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        NotFoundResourceException exception = assertThrows(NotFoundResourceException.class, () -> {
            productCommandService.deleteProduct(1L);
        });

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(NOT_FOUND_PRODUCT, exception.getMessage());

        verify(productRepository, never()).delete(any(ProductEntity.class));
        verify(productEventPublisher, never()).publishDeleteProductEvent(any(ProductInfo.class));
    }

    @DisplayName("상품 추가 성공 테스트")
    @Test
    void addProduct_returnSuccess() {
        // given
        ProductInsertDto productInsertDto = MockData.getProductInsertDto();
        ProductEntity productEntity = MockData.getProductEntity();
        given(brandRepository.findById(anyLong())).willReturn(Optional.of(productEntity.getBrand()));
        given(productRepository.save(any(ProductEntity.class))).willReturn(productEntity);

        // when
        ProductInfo result = productCommandService.addProduct(productInsertDto);

        // then
        assertNotNull(result);
        assertEquals(productEntity.getProductName(), result.getProductName());
        assertEquals(productEntity.getProductPrice(), result.getProductPrice());
        assertEquals(productEntity.getBrand().getBrandName(), result.getBrandInfo().getBrandName());
    }

    @DisplayName("상품 추가 - 브랜드가 없는 경우 테스트")
    @Test
    void addProduct_BrandNotFound() {
        // given
        ProductInsertDto productInsertDto = MockData.getProductInsertDto();
        when(brandRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        NotFoundResourceException exception = assertThrows(NotFoundResourceException.class, () -> {
            productCommandService.addProduct(productInsertDto);
        });

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(NOT_FOUND_BRAND, exception.getMessage());

        verify(productRepository, never()).save(any(ProductEntity.class));
        verify(productEventPublisher, never()).publishInsertProductEvent(any(ProductInfo.class));
    }

    @DisplayName("브랜드 추가 성공 테스트")
    @Test
    void addBrand_returnSuccess() {
        // given
        String brandName = "brandA";
        BrandInsertDto brandInsertDto = MockData.getBrandInsertDto(brandName);
        BrandEntity brandEntity = MockData.getBrandEntity(brandName);
        given(brandRepository.save(any(BrandEntity.class))).willReturn(brandEntity);

        // when
        BrandInfo result = productCommandService.addBrand(brandInsertDto);

        // then
        assertNotNull(result);
        assertEquals(brandEntity.getBrandName(), result.getBrandName());
    }

    @DisplayName("브랜드 수정 성공 테스트")
    @Test
    void updateBrand_returnSuccess() {
        // given
        String oldBrandName = "brandA";
        String newBrandName = "brandB";
        BrandUpdateDto brandUpdateDto = MockData.getBrandUpdateDto(newBrandName);
        BrandEntity existingBrand = MockData.getBrandEntity(oldBrandName);

        given(brandRepository.findById(anyLong())).willReturn(Optional.of(existingBrand));
        given(brandRepository.save(existingBrand)).willReturn(existingBrand);

        // when
        BrandInfo result = productCommandService.updateBrand(brandUpdateDto);

        // then
        assertNotNull(result);
        assertEquals(newBrandName, result.getBrandName());

        verify(brandRepository, times(1)).save(existingBrand);
        verify(productEventPublisher, times(1)).publishUpdateBrandEvent(oldBrandName,
            newBrandName);
    }

    @DisplayName("브랜드 수정 - 브랜드가 없는 경우 테스트")
    @Test
    void updateBrand_BrandNotFound() {
        // given
        BrandUpdateDto brandUpdateDto = MockData.getBrandUpdateDto("brandB");
        when(brandRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        NotFoundResourceException exception = assertThrows(NotFoundResourceException.class, () -> {
            productCommandService.updateBrand(brandUpdateDto);
        });

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(NOT_FOUND_BRAND, exception.getMessage());

        verify(brandRepository, never()).save(any(BrandEntity.class));
    }

    @DisplayName("브랜드 삭제 성공 테스트")
    @Test
    void deleteBrand_returnSuccess() {
        // given
        BrandEntity existingBrand = MockData.getBrandEntity("brandA");

        given(brandRepository.findById(anyLong())).willReturn(Optional.of(existingBrand));

        // when
        productCommandService.deleteBrand(1L);

        // then
        verify(brandRepository, times(1)).delete(existingBrand);
    }

    @DisplayName("브랜드 삭제 - 브랜드가 없는 경우 테스트")
    @Test
    void deleteBrand_BrandNotFound() {
        // given
        when(brandRepository.findById(anyLong())).thenReturn(Optional.empty());

        // when
        NotFoundResourceException exception = assertThrows(NotFoundResourceException.class, () -> {
            productCommandService.deleteBrand(1L);
        });

        // then
        assertEquals(HttpStatus.NOT_FOUND, exception.getHttpStatus());
        assertEquals(NOT_FOUND_BRAND, exception.getMessage());

        verify(brandRepository, never()).delete(any(BrandEntity.class));
    }

}