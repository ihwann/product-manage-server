package com.musinsa.productmanageserver.product.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.mock.MockData;
import com.musinsa.productmanageserver.product.dto.external.request.BrandInsertRequest;
import com.musinsa.productmanageserver.product.dto.external.request.BrandUpdateRequest;
import com.musinsa.productmanageserver.product.dto.external.request.ProductInsertRequest;
import com.musinsa.productmanageserver.product.dto.external.request.ProductUpdateRequest;
import com.musinsa.productmanageserver.product.service.ProductCommandService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(AdminController.class)
class AdminControllerTest {

    @MockBean
    private ProductCommandService productCommandService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("유효하지 않은 상품 추가 api 요청")
    @Test
    void addProduct_invalidRequest_returnSuccess() throws Exception {
        // given
        ProductInsertRequest request = mockProductInsertRequest(
            1L,
            null,
            "상품명",
            10000
        );

        // when
        ResultActions resultActions = requestInsertProduct(request);

        // then
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.resultCode").value("FAIL"))
            .andExpect(jsonPath("$.data").value("잘못된 요청 입니다."));
    }

    @DisplayName("상품 추가 api 정상 요청")
    @Test
    void addProduct_returnSuccess() throws Exception {
        // given
        ProductInsertRequest request = mockProductInsertRequest(
            1L,
            Category.TOP,
            "ProductA",
            10000
        );
        given(productCommandService.addProduct(any())).willReturn(MockData.getProductInfo());

        // when
        ResultActions resultActions = requestInsertProduct(request);

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.data.productId").value(1))
            .andExpect(jsonPath("$.data.productName").value("ProductA"));
    }

    @DisplayName("상품 수정 api 정상 요청")
    @Test
    void updateProduct_returnSuccess() throws Exception {
        // given
        ProductUpdateRequest request = mockProductUpdateRequest(10000);
        given(productCommandService.updateProduct(any())).willReturn(MockData.getProductInfo());

        // when
        ResultActions resultActions = requestUpdateProduct(1L, request);

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.data.productId").value(1))
            .andExpect(jsonPath("$.data.price").value(10000));
    }

    @DisplayName("상품 삭제 api 정상 요청")
    @Test
    void deleteProduct_returnSuccess() throws Exception {
        // given
        Long productId = 1L;

        // when
        ResultActions resultActions = requestDeleteProduct(productId);

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"));
    }

    @DisplayName("브랜드 추가 api 정상 요청")
    @Test
    void addBrand_returnSuccess() throws Exception {
        // given
        BrandInsertRequest brandInsertRequest = mockBrandInsertRequest("BrandA");
        given(productCommandService.addBrand(any())).willReturn(MockData.getBrandInfo());

        // when
        ResultActions resultActions = requestInsertBrand(brandInsertRequest);

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.data.brandId").value(1))
            .andExpect(jsonPath("$.data.brandName").value("BrandA"));
    }

    @DisplayName("브랜드 추가 api 잘못된 요청")
    @Test
    void addBrand_invalidRequest_returnSuccess() throws Exception {
        // given
        BrandInsertRequest brandInsertRequest = mockBrandInsertRequest(null);

        // when
        ResultActions resultActions = requestInsertBrand(brandInsertRequest);

        // then
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.resultCode").value("FAIL"))
            .andExpect(jsonPath("$.data").value("잘못된 요청 입니다."));
    }

    @DisplayName("브랜드 수정 api 잘못된 요청")
    @Test
    void updateBrand_invalidRequest_returnSuccess() throws Exception {
        // given
        BrandUpdateRequest brandUpdateRequest = mockBrandUpdateRequest(null);

        // when
        ResultActions resultActions = requestUpdateBrand(1L, brandUpdateRequest);

        // then
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.resultCode").value("FAIL"))
            .andExpect(jsonPath("$.data").value("잘못된 요청 입니다."));
    }

    @DisplayName("브랜드 수정 api 정상 요청")
    @Test
    void updateBrand_returnSuccess() throws Exception {
        // given
        BrandUpdateRequest brandUpdateRequest = mockBrandUpdateRequest("BrandA");
        given(productCommandService.updateBrand(any())).willReturn(MockData.getBrandInfo());

        // when
        ResultActions resultActions = requestUpdateBrand(1L, brandUpdateRequest);

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"))
            .andExpect(jsonPath("$.data.brandId").value(1))
            .andExpect(jsonPath("$.data.brandName").value("BrandA"));
    }

    @DisplayName("브랜드 삭제 api 정상 요청")
    @Test
    void deleteBrand_returnSuccess() throws Exception {
        // given
        Long brandId = 1L;

        // when
        ResultActions resultActions = requestDeleteBrand(brandId);

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.resultCode").value("SUCCESS"));
    }

    private ResultActions requestDeleteProduct(Long productId) throws Exception {
        return mvc.perform(delete("/api/v1/admin/products/{productId}", productId));
    }

    private ResultActions requestInsertProduct(ProductInsertRequest body)
        throws Exception {
        return mvc.perform(post("/api/v1/admin/products")
            .content(objectMapper.writeValueAsString(body))
            .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions requestUpdateProduct(Long productId, ProductUpdateRequest body)
        throws Exception {
        return mvc.perform(put("/api/v1/admin/products/{productId}", productId)
            .content(objectMapper.writeValueAsString(body))
            .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions requestInsertBrand(BrandInsertRequest body)
        throws Exception {
        return mvc.perform(post("/api/v1/admin/brands")
            .content(objectMapper.writeValueAsString(body))
            .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions requestUpdateBrand(Long bandId, BrandUpdateRequest body)
        throws Exception {
        return mvc.perform(put("/api/v1/admin/brands/{brandId}", bandId)
            .content(objectMapper.writeValueAsString(body))
            .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions requestDeleteBrand(Long bandId)
        throws Exception {
        return mvc.perform(delete("/api/v1/admin/brands/{brandId}", bandId));
    }

    private ProductInsertRequest mockProductInsertRequest(Long brandId, Category category,
        String productName, Integer productPrice) {
        return new ProductInsertRequest(brandId, category, productName, productPrice);
    }

    private ProductUpdateRequest mockProductUpdateRequest(int price) {
        return new ProductUpdateRequest(price);
    }

    private BrandInsertRequest mockBrandInsertRequest(String brandName) {
        return new BrandInsertRequest(brandName);
    }

    private BrandUpdateRequest mockBrandUpdateRequest(String brandName) {
        return new BrandUpdateRequest(brandName);
    }


}