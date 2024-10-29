package com.musinsa.productmanageserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class ProductManageServerApplication {

    public static void main(String[] args) {
        SpringApplication.run(ProductManageServerApplication.class, args);
    }

}
