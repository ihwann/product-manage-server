package com.musinsa.productmanageserver.infrastucture.redis.component;

import com.musinsa.productmanageserver.event.component.ProductEventPublisher;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import com.musinsa.productmanageserver.product.repository.ProductRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WarmUpCacheComponent implements ApplicationRunner {

    private final ProductRepository productRepository;
    private final ProductEventPublisher productEventPublisher;

    @Override
    public void run(ApplicationArguments args) {
        List<ProductInfo> productInfoList = productRepository.findAllWithBrand()
            .stream()
            .map(entity -> ProductInfo.fromEntityBuilder()
                .entity(entity)
                .build())
            .toList();

        productInfoList.forEach(this::publish);
    }

    public void publish(ProductInfo productInfo) {
        productEventPublisher.publishInsertProductEvent(productInfo);
    }
}
