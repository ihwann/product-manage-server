package com.musinsa.productmanageserver.event.component;

import com.musinsa.productmanageserver.event.message.BrandUpdateEvent;
import com.musinsa.productmanageserver.event.message.ProductDeleteEvent;
import com.musinsa.productmanageserver.event.message.ProductInsertEvent;
import com.musinsa.productmanageserver.event.message.ProductUpdateEvent;
import com.musinsa.productmanageserver.infrastucture.redis.service.ProductPriceComparisonManager;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Slf4j
@Component
@RequiredArgsConstructor
public class ProductEventListener {

    private final ProductPriceComparisonManager productPriceComparisonManager;

    /**
     * 상품 등록 이벤트 핸들러
     *
     * @param productInsertEvent 상품 등록 이벤트 메세지
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void insertEventHandle(ProductInsertEvent productInsertEvent) {
        ProductInfo productInfo = productInsertEvent.productInfo();

        log.info("insertEventHandle subscribe message : {}", productInfo.toString());

        addProductInfoToSortedSet(productInfo);
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

        log.info("updateEventHandle subscribe message : {}", productInfo.toString());

        addProductInfoToSortedSet(productInfo);
    }

    /**
     * 상품 삭제 이벤트 핸들러
     *
     * @param productDeleteEvent 상품 삭제 이벤트 메세지
     */
    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void deleteEventHandle(ProductDeleteEvent productDeleteEvent) {
        ProductInfo productInfo = productDeleteEvent.productInfo();

        log.info("deleteEventHandle subscribe message : {}", productInfo.toString());

        deleteProductInfoToSortedSet(productInfo);
    }

    @Async
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void updateBrandEventHandle(BrandUpdateEvent brandUpdateEvent) {
        String oldBrandName = brandUpdateEvent.oldBrandName();
        String newBrandName = brandUpdateEvent.newBrandName();

        log.info("updateBrandEventHandle subscribe message : {} -> {}", oldBrandName, newBrandName);

        productPriceComparisonManager.updateBrandName(oldBrandName, newBrandName);
    }

    /**
     * 상품 정보를 정렬된 셋에 추가
     * @param productInfo 상품 정보
     */
    private void addProductInfoToSortedSet(ProductInfo productInfo) {
        // 카테고리 셋에 추가
        productPriceComparisonManager.addToCategorySet(productInfo);

        // 브랜드 카테고리 셋에 추가
        Integer rank = productPriceComparisonManager.addAndGetRankBrandCategorySet(productInfo);

        // 브랜드 카테고리 셋에 최저가 상품이 등록되었다면
        // 브랜드 셋에 갱신
        if (isTopRank(rank)) {
            productPriceComparisonManager.refreshBrandCategorySet(productInfo.getBrandInfo().getBrandName());
        }
    }

    /**
     * 최저가 상품인지 확인
     * @param rank 랭크
     * @return 최저가 상품 여부
     */
    private boolean isTopRank(Integer rank) {
        return rank == 0;
    }

    /**
     * 상품 정보를 정렬된 셋에서 삭제
     * @param productInfo 상품 정보
     */
    private void deleteProductInfoToSortedSet(ProductInfo productInfo) {
        // 카테고리 셋에 삭제
        productPriceComparisonManager.deleteFromCategorySet(productInfo);

        // 브랜드 카테고리 셋에 삭제
        productPriceComparisonManager.deleteAndGetRankBrandCategorySet(productInfo);

        // 브랜드 셋에 갱신
        productPriceComparisonManager.refreshBrandCategorySet(productInfo.getBrandInfo().getBrandName());
    }
}
