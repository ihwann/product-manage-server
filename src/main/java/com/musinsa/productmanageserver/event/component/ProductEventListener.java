package com.musinsa.productmanageserver.event.component;

import com.musinsa.productmanageserver.event.message.ProductInsertEvent;
import com.musinsa.productmanageserver.event.message.ProductUpdateEvent;
import com.musinsa.productmanageserver.infrastucture.redis.service.ProductSortManager;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private final ProductSortManager productSortManager;

    /**
     * 상품 등록 이벤트 핸들러
     *
     * @param productInsertEvent 상품 등록 이벤트 메세지
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.BEFORE_COMMIT)
    public void insertEventHandle(ProductInsertEvent productInsertEvent) {
        ProductInfo productInfo = productInsertEvent.productInfo();

        log.info("thread : {}, subscribe message : {}", Thread.currentThread().getName(),
            productInfo.toString());

        addProductInfoToSortedSet(productInfo);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void test(String msg) {
        System.out.println("===============> " + msg);
    }

    /**
     * 상품 변경 이벤트 핸들러
     *
     * @param productUpdateEvent 상품 변경 이벤트 메세지
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateEventHandle(ProductUpdateEvent productUpdateEvent) {
        ProductInfo productInfo = productUpdateEvent.productInfo();

        log.info("thread : {}, subscribe message : {}", Thread.currentThread().getName(),
            productInfo.toString());

        addProductInfoToSortedSet(productInfo);
    }

    /**
     * 상품 정보를 정렬된 셋에 추가
     * @param productInfo 상품 정보
     */
    private void addProductInfoToSortedSet(ProductInfo productInfo) {
        // 카테고리 셋에 추가
        productSortManager.addToCategorySet(productInfo);

        // 브랜드 카테고리 셋에 추가
        Integer rank = productSortManager.addAndGetRankBrandCategorySet(productInfo);

        // 브랜드 카테고리 셋에 최저가 상품이 등록되었다면
        // 브랜드 셋에 추가
        if (rank == 0) {
            productSortManager.addToBrandSet(productInfo.getBrandInfo().getBrandName(),
                productInfo.getProductPrice());
        }
    }
}
