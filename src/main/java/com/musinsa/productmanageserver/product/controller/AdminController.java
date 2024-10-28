package com.musinsa.productmanageserver.product.controller;

import com.musinsa.productmanageserver.common.dto.BaseResponse;
import com.musinsa.productmanageserver.product.dto.external.request.ProductInsertRequest;
import com.musinsa.productmanageserver.product.dto.external.request.ProductUpdateRequest;
import com.musinsa.productmanageserver.product.dto.external.response.ProductResponse;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import com.musinsa.productmanageserver.product.dto.internal.ProductInsertDto;
import com.musinsa.productmanageserver.product.dto.internal.ProductUpdateDto;
import com.musinsa.productmanageserver.product.service.ProductCommandService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
public class AdminController {

    private final ProductCommandService productCommandService;

    @Operation(summary = "상품 추가", description = "상품을 추가합니다.")
    @PostMapping("/products")
    public ResponseEntity<BaseResponse<ProductResponse>> addProduct(
        @Parameter(description = "상품 정보", required = true)
        @RequestBody ProductInsertRequest productInsertRequest
    ) {

        ProductInsertDto insertDto = ProductInsertDto.fromRequestBuilder()
            .request(productInsertRequest)
            .build();

        ProductInfo productInfo = productCommandService.addProduct(insertDto);

        return ResponseEntity.ok(
            new BaseResponse<>(BaseResponse.SUCCESS, ProductResponse.from(productInfo)));
    }

    @Operation(summary = "상품 수정", description = "상품을 수정합니다.")
    @PutMapping("/products/{id}")
    public ResponseEntity<BaseResponse<ProductResponse>> updateProduct(
        @Parameter(name = "id", description = "상품 ID", required = true, in = ParameterIn.PATH)
        @PathVariable("id") Long id,
        @Parameter(description = "상품 정보", required = true)
        @RequestBody ProductUpdateRequest productRequest
    ) {

        ProductUpdateDto updateDto = ProductUpdateDto.builder()
            .productId(id)
            .productPrice(productRequest.price())
            .build();

        ProductInfo productInfo = productCommandService.updateProduct(updateDto);

        return ResponseEntity.ok(
            new BaseResponse<>(BaseResponse.SUCCESS, ProductResponse.from(productInfo)));
    }

    @Operation(summary = "상품 삭제", description = "상품을 삭제합니다.")
    @DeleteMapping("/products/{id}")
    public ResponseEntity<BaseResponse> deleteProduct(
        @Parameter(name = "id", description = "상품 ID", required = true, in = ParameterIn.PATH)
        @PathVariable("id") Long id
    ) {
        productCommandService.deleteProduct(id);
        return ResponseEntity.ok(new BaseResponse(BaseResponse.SUCCESS, null));
    }

    @Operation(summary = "브랜드 추가", description = "브랜드를 추가합니다.")
    @PostMapping("/brands")
    public ResponseEntity<BaseResponse> addBrand() {
        return ResponseEntity.ok(new BaseResponse(BaseResponse.SUCCESS, null));
    }

    @Operation(summary = "브랜드 수정", description = "브랜드를 수정합니다.")
    @PutMapping("/brands")
    public ResponseEntity<BaseResponse> updateBrand() {
        return ResponseEntity.ok(new BaseResponse(BaseResponse.SUCCESS, null));
    }

    @Operation(summary = "브랜드 삭제", description = "브랜드를 삭제합니다.")
    @DeleteMapping("/brands")
    public ResponseEntity<BaseResponse> deleteBrand() {
        return ResponseEntity.ok(new BaseResponse(BaseResponse.SUCCESS, null));
    }
}
