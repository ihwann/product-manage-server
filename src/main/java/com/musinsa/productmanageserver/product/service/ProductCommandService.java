package com.musinsa.productmanageserver.product.service;

import com.musinsa.productmanageserver.exception.NotFoundResourceException;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import com.musinsa.productmanageserver.product.dto.internal.ProductInsertDto;
import com.musinsa.productmanageserver.product.dto.internal.ProductUpdateDto;
import com.musinsa.productmanageserver.product.model.BrandEntity;
import com.musinsa.productmanageserver.product.model.ProductEntity;
import com.musinsa.productmanageserver.product.repository.BrandRepository;
import com.musinsa.productmanageserver.product.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 커맨드 서비스
 */
@Service
@RequiredArgsConstructor
public class ProductCommandService {

    private final ProductRepository productRepository;
    private final BrandRepository brandRepository;

    /**
     * 상품 수정
     *
     * @param updateDto 상품 수정 요청
     * @return 수정된 상품 정보
     */
    @Transactional
    public ProductInfo updateProduct(ProductUpdateDto updateDto) {
        ProductEntity productEntity = productRepository.findById(updateDto.getProductId())
            .map(product -> {
                product.updatePrice(updateDto.getProductPrice());
                return saveProductEntity(product);
            })
            .orElseThrow(() -> new NotFoundResourceException(HttpStatus.NOT_FOUND,
                "상품이 존재하지 않습니다. 상품 ID: " + updateDto.getProductId()));

        return ProductInfo.fromEntityBuilder()
            .entity(productEntity)
            .build();
    }

    /**
     * 상품 삭제
     *
     * @param productId 상품 ID
     */
    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.findById(productId)
            .ifPresent(productRepository::delete);
    }

    /**
     * 상품 추가
     *
     * @param productInsertDto 상품 추가 요청
     * @return 추가된 상품 정보
     */
    @Transactional
    public ProductInfo addProduct(ProductInsertDto productInsertDto) {
        BrandEntity brandEntity = brandRepository.findById(productInsertDto.getBrandId())
            .orElseThrow(() -> new NotFoundResourceException(HttpStatus.NOT_FOUND,
                "브랜드가 존재하지 않습니다. 브랜드 ID: " + productInsertDto.getBrandId()));

        ProductEntity productEntity = newProductEntity(productInsertDto, brandEntity);

        saveProductEntity(productEntity);

        return ProductInfo.fromEntityBuilder()
            .entity(productEntity)
            .build();
    }

    /**
     * 상품 엔티티 생성
     *
     * @param productInsertDto 상품 추가 요청
     * @param brandEntity      브랜드 엔티티
     * @return 상품 엔티티
     */
    private ProductEntity newProductEntity(ProductInsertDto productInsertDto,
        BrandEntity brandEntity) {

        return ProductEntity.NewBuilder()
            .insertDto(productInsertDto)
            .brandEntity(brandEntity)
            .build();
    }


    public ProductEntity saveProductEntity(ProductEntity product) {
        return productRepository.save(product);
    }
}
