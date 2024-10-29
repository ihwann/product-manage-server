package com.musinsa.productmanageserver.infrastucture.redis.component;

import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import com.musinsa.productmanageserver.product.repository.ProductRepository;
import com.musinsa.productmanageserver.product.service.ProductCommandService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class WarmUpCacheComponent implements ApplicationRunner {

    private final ProductRepository productRepository;
    private final ProductCommandService productCommandService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        List<ProductInfo> productInfoList = productRepository.findAllWithBrand()
            .stream()
            .map(entity -> ProductInfo.fromEntityBuilder()
                .entity(entity)
                .build())
            .toList();

        productInfoList.forEach(this::publish);
    }

    public void publish(ProductInfo productInfo) {
        productCommandService.publishInsertProductEvent(productInfo);
    }
}
