package com.musinsa.productmanageserver.event.component;

import com.musinsa.productmanageserver.event.message.BrandUpdateEvent;
import com.musinsa.productmanageserver.event.message.ProductDeleteEvent;
import com.musinsa.productmanageserver.event.message.ProductInsertEvent;
import com.musinsa.productmanageserver.event.message.ProductUpdateEvent;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * 상품 이벤트 발행자
 */
@Component
@RequiredArgsConstructor
public class ProductEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    /**
     * 상품 등록 이벤트 발행
     * @param productInfo 상품 정보
     */
    @Transactional
    public void publishInsertProductEvent(ProductInfo productInfo) {
        applicationEventPublisher.publishEvent(new ProductInsertEvent(productInfo));
    }

    /**
     * 상품 수정 이벤트 발행
     * @param productInfo 상품 정보
     */
    @Transactional
    public void publishUpdateProductEvent(ProductInfo productInfo) {
        applicationEventPublisher.publishEvent(new ProductUpdateEvent(productInfo));
    }

    /**
     * 상품 삭제 이벤트 발행
     * @param productInfo 상품 정보
     */
    @Transactional
    public void publishDeleteProductEvent(ProductInfo productInfo) {
        applicationEventPublisher.publishEvent(new ProductDeleteEvent(productInfo));
    }

    /**
     * 브랜드 수정 이벤트 발행
     * @param oldBrandName 구 브랜드명
     * @param newBrandName 신규 브랜드명
     */
    @Transactional
    public void publishUpdateBrandEvent(String oldBrandName, String newBrandName) {
        applicationEventPublisher.publishEvent(new BrandUpdateEvent(oldBrandName, newBrandName));
    }
}
