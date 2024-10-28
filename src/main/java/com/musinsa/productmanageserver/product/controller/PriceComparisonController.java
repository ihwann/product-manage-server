package com.musinsa.productmanageserver.product.controller;

import com.musinsa.productmanageserver.common.dto.BaseResponse;
import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.common.util.MoneyUtil;
import com.musinsa.productmanageserver.product.dto.external.response.BrandPriceResponse;
import com.musinsa.productmanageserver.product.dto.external.response.LowestAndHighestPriceProduct;
import com.musinsa.productmanageserver.product.dto.external.response.LowestPriceBrandByCategory;
import com.musinsa.productmanageserver.product.dto.external.response.LowestPriceBrandResponse;
import com.musinsa.productmanageserver.product.dto.external.response.LowestPriceBrandsByCategoryResponse;
import com.musinsa.productmanageserver.product.dto.internal.LowestBrandInfo;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import com.musinsa.productmanageserver.product.service.ProductQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
public class PriceComparisonController {

    private final ProductQueryService productQueryService;

    @Operation(summary = "카테고리별 최저가격 브랜드와 총액 조회", description = "카테고리별 최저가격 브랜드와 총액 조회합니다.")
    @GetMapping("/categories/prices/lowest")
    public ResponseEntity<BaseResponse<LowestPriceBrandsByCategoryResponse>> getCheapestBrandsByCategory(
    ) {

        List<ProductInfo> cheapestProductCategory = productQueryService.getLowestPriceBrandsByCategory();

        List<LowestPriceBrandByCategory> cheapestBrandsByCategory = cheapestProductCategory.stream()
            .map(productInfo -> LowestPriceBrandByCategory.fromProductInfoBuilder()
                .productInfo(productInfo)
                .build())
            .toList();

        int totalPrice = cheapestProductCategory.stream()
            .mapToInt(ProductInfo::getProductPrice)
            .sum();

        return ResponseEntity.ok(
            new BaseResponse<>(BaseResponse.SUCCESS,
                new LowestPriceBrandsByCategoryResponse(cheapestBrandsByCategory,
                    MoneyUtil.convertStringFormat(totalPrice))));
    }

    @Operation(summary = "카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격 조회", description = "카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격을 조회합니다.")
    @GetMapping("/categories/{category}/prices/lowest-and-highest")
    public ResponseEntity<BaseResponse<LowestAndHighestPriceProduct>> getCheapestBrandsByCategory(
        @Parameter(name = "category", description = "카테고리 이름", required = true, in = ParameterIn.PATH)
        @PathVariable("category") String categoryName
    ) {

        Category category = Category.findByName(categoryName);

        List<BrandPriceResponse> lowestPriceBrandList = productQueryService.getLowestPriceAllProductByCategory(
                category)
            .stream()
            .map(productInfo -> BrandPriceResponse.of(productInfo.getBrandInfo().getBrandName(),
                productInfo.getProductPrice()))
            .toList();

        List<BrandPriceResponse> highestPriceBrandList = productQueryService.getHighestPriceAllProductByCategory(
                category)
            .stream()
            .map(productInfo -> BrandPriceResponse.of(productInfo.getBrandInfo().getBrandName(),
                productInfo.getProductPrice()))
            .toList();

        return ResponseEntity.ok(
            new BaseResponse<>(BaseResponse.SUCCESS,
                new LowestAndHighestPriceProduct(
                    category,
                    lowestPriceBrandList,
                    highestPriceBrandList)));
    }

    @Operation(summary = "최저가격에 판매하는 브랜드와 카테고리별 상품 가격 조회", description = "카최저가격에 판매하는 브랜드와 카테고리별 상품 가격 조회합니다.")
    @GetMapping("/lowest-price-brand")
    public ResponseEntity<BaseResponse<LowestPriceBrandResponse>> getLowestPriceBrand() {

        LowestBrandInfo lowestPriceBrand = productQueryService.getLowestPriceBrand();

        return ResponseEntity.ok(
            new BaseResponse<>(BaseResponse.SUCCESS,
                new LowestPriceBrandResponse(lowestPriceBrand)));
    }
}
