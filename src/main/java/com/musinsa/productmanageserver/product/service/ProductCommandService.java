package com.musinsa.productmanageserver.product.service;

import com.musinsa.productmanageserver.event.message.ProductDeleteEvent;
import com.musinsa.productmanageserver.event.message.ProductInsertEvent;
import com.musinsa.productmanageserver.event.message.ProductUpdateEvent;
import com.musinsa.productmanageserver.exception.NotFoundResourceException;
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
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher applicationEventPublisher;

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

        ProductInfo productInfo = ProductInfo.fromEntityBuilder()
            .entity(productEntity)
            .build();

        publishUpdateProductEvent(productInfo);

        return productInfo;
    }

    /**
     * 상품 삭제
     *
     * @param productId 상품 ID
     */
    @Transactional
    public void deleteProduct(Long productId) {
        productRepository.findById(productId)
            .ifPresent(productEntity -> {
                productRepository.delete(productEntity);

                publishDeleteProductEvent(ProductInfo.fromEntityBuilder()
                    .entity(productEntity)
                    .build());
            });
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
                "브랜드가 존재하지 않습니다."));

        ProductEntity productEntity = newProductEntity(productInsertDto, brandEntity);

        saveProductEntity(productEntity);

        ProductInfo productInfo = ProductInfo.fromEntityBuilder()
            .entity(productEntity)
            .build();

        publishInsertProductEvent(productInfo);

        return productInfo;
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

        return ProductEntity.newBuilder()
            .insertDto(productInsertDto)
            .brandEntity(brandEntity)
            .build();
    }


    public ProductEntity saveProductEntity(ProductEntity product) {
        return productRepository.save(product);
    }

    /**
     * 브랜드 추가
     *
     * @param insertDto 브랜드 추가 요청
     * @return 추가된 브랜드 정보
     */
    @Transactional
    public BrandInfo addBrand(BrandInsertDto insertDto) {
        BrandEntity brandEntity = BrandEntity.newBuilder()
            .insertDto(insertDto)
            .build();

        saveBrandEntity(brandEntity);

        return BrandInfo.fromEntityBuilder()
            .entity(brandEntity)
            .build();
    }

    /**
     * 브랜드 정보 수정
     *
     * @param updateDto 브랜드 수정 요청
     * @return 수정된 브랜드 정보
     */
    @Transactional
    public BrandInfo updateBrand(BrandUpdateDto updateDto) {
        BrandEntity brandEntity = brandRepository.findById(updateDto.getBrandId())
            .map(brand -> {
                brand.updateBrandName(updateDto.getBrandName());
                return saveBrandEntity(brand);
            })
            .orElseThrow(() -> new NotFoundResourceException(HttpStatus.NOT_FOUND,
                "브랜드가 존재하지 않습니다."));

        return BrandInfo.fromEntityBuilder()
            .entity(brandEntity)
            .build();
    }

    /**
     * 브랜드 삭제
     *
     * @param brandId 브랜드 ID
     */
    @Transactional
    public void deleteBrand(Long brandId) {
        brandRepository.findById(brandId)
            .ifPresentOrElse(
                brandEntity -> {
                    brandRepository.delete(brandEntity);

                    productRepository.findAllByBrandId(brandId)
                        .forEach(productEntity -> publishDeleteProductEvent(
                            ProductInfo.fromEntityBuilder()
                                .entity(productEntity)
                                .build()));
                },
                () -> {
                    throw new NotFoundResourceException(HttpStatus.NOT_FOUND,
                        "브랜드가 존재하지 않습니다.");
                });
    }


    public BrandEntity saveBrandEntity(BrandEntity brand) {
        return brandRepository.save(brand);
    }

    @Transactional
    public void publishInsertProductEvent(ProductInfo productInfo) {
        applicationEventPublisher.publishEvent(new ProductInsertEvent(productInfo));
    }

    @Transactional
    public void publishUpdateProductEvent(ProductInfo productInfo) {
        applicationEventPublisher.publishEvent(new ProductUpdateEvent(productInfo));
    }

    @Transactional
    public void publishDeleteProductEvent(ProductInfo productInfo) {
        applicationEventPublisher.publishEvent(new ProductDeleteEvent(productInfo));
    }
}
