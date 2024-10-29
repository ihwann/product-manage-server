package com.musinsa.productmanageserver.infrastucture.redis.component;

import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import com.musinsa.productmanageserver.product.repository.ProductRepository;
import com.musinsa.productmanageserver.product.service.ProductCommandService;
import jakarta.annotation.PostConstruct;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class SetupCacheComponent {

    private final ProductRepository productRepository;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final ProductCommandService productCommandService;

    @PostConstruct
    public void setup() {
        List<ProductInfo> productInfoList = productRepository.findAllWithBrand()
            .stream()
            .map(entity -> ProductInfo.fromEntityBuilder()
                .entity(entity)
                .build())
            .toList();

        productInfoList.forEach(this::publish);
    }

    public void publish(ProductInfo productInfo) {
        productCommandService.publish(productInfo);
    }
}
