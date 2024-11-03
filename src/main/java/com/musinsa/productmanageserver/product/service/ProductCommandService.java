package com.musinsa.productmanageserver.product.service;

import static com.musinsa.productmanageserver.common.constant.Constants.ErrorMessage.NOT_FOUND_BRAND;
import static com.musinsa.productmanageserver.common.constant.Constants.ErrorMessage.NOT_FOUND_PRODUCT;

import com.musinsa.productmanageserver.event.component.ProductEventPublisher;
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
    private final ProductEventPublisher productEventPublisher;

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
                "상품이 존재하지 않습니다."));

        ProductInfo productInfo = buildProductInfo(productEntity);

        productEventPublisher.publishUpdateProductEvent(productInfo);

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
            .ifPresentOrElse(
                productEntity -> {
                    productRepository.delete(productEntity);
                    productEventPublisher.publishDeleteProductEvent(buildProductInfo(productEntity));
                },
                () -> {
                    throw new NotFoundResourceException(HttpStatus.NOT_FOUND, NOT_FOUND_PRODUCT);
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
                NOT_FOUND_BRAND));

        ProductEntity productEntity = newProductEntity(productInsertDto, brandEntity);

        saveProductEntity(productEntity);

        ProductInfo productInfo = buildProductInfo(productEntity);

        productEventPublisher.publishInsertProductEvent(productInfo);

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
                productEventPublisher.publishUpdateBrandEvent(brand.getBrandName(), updateDto.getBrandName());
                brand.updateBrandName(updateDto.getBrandName());
                return saveBrandEntity(brand);
            })
            .orElseThrow(() -> new NotFoundResourceException(HttpStatus.NOT_FOUND, NOT_FOUND_BRAND));

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
                        .forEach(productEntity -> productEventPublisher.publishDeleteProductEvent(
                            buildProductInfo(productEntity)));
                },
                () -> {
                    throw new NotFoundResourceException(HttpStatus.NOT_FOUND, NOT_FOUND_BRAND);
                });
    }


    public BrandEntity saveBrandEntity(BrandEntity brand) {
        return brandRepository.save(brand);
    }

    private ProductInfo buildProductInfo(ProductEntity productEntity) {
        return ProductInfo.fromEntityBuilder()
            .entity(productEntity)
            .build();
    }
}
