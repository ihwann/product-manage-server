package com.musinsa.productmanageserver.product.controller;

import static com.musinsa.productmanageserver.common.constant.Constants.ErrorMessage.INVALID_CATEGORY;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.mock.MockData;
import com.musinsa.productmanageserver.product.service.ProductQueryService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

@WebMvcTest(PriceComparisonController.class)
class PriceComparisonControllerTest {

    @MockBean
    private ProductQueryService productQueryService;

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper objectMapper;

    @DisplayName("카테고리별 최저가격 브랜드와 총액 조회")
    @Test
    void getLowestPriceBrandByCategory_returnSuccess() throws Exception {
        // given
        given(productQueryService.getLowestPriceBrandsByCategory()).willReturn(
            MockData.getProductInfoList());

        // when
        ResultActions resultActions = requestLowestBrandByCategory();

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.data.lowestPriceBrandList", hasSize(8)))
            .andExpect(jsonPath("$.data.lowestPriceBrandList[*].category").exists())
            .andExpect(jsonPath("$.data.lowestPriceBrandList[*].brandName").exists())
            .andExpect(jsonPath("$.data.lowestPriceBrandList[*].price").exists())
            .andExpect(jsonPath("$.data.totalPrice").value("36,000"));

    }

    @DisplayName("카테고리별 최저가격 브랜드와 총액 조회 - 카테고리별 최저가격 브랜드가 없는 경우")
    @Test
    void getLowestPriceBrandByCategory_noData_returnSuccess() throws Exception {
        // given
        given(productQueryService.getLowestPriceBrandsByCategory()).willReturn(List.of());

        // when
        ResultActions resultActions = requestLowestBrandByCategory();

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.data.lowestPriceBrandList", hasSize(0)))
            .andExpect(jsonPath("$.data.totalPrice").value("0"));
    }

    @DisplayName("카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격 조회")
    @Test
    void getLowestAndHighestPriceBrandByCategory_returnSuccess() throws Exception {
        // given
        Category category = Category.TOP;

        given(
            productQueryService.getLowestPriceAllProductByCategory(any(Category.class))).willReturn(
            MockData.getLowestPriceAllProductByCategory(category));
        given(productQueryService.getHighestPriceAllProductByCategory(
            any(Category.class))).willReturn(
            MockData.getHighestPriceAllProductByCategory(category));

        // when
        ResultActions resultActions = requestLowestAndHighestBrandByCategory(category.name());

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.data.category").value(category.name()))
            .andExpect(jsonPath("$.data.lowestPriceBrandList", hasSize(2)))
            .andExpect(jsonPath("$.data.lowestPriceBrandList[*].brandName").exists())
            .andExpect(jsonPath("$.data.lowestPriceBrandList[*].price").exists())
            .andExpect(jsonPath("$.data.highestPriceBrandList", hasSize(2)))
            .andExpect(jsonPath("$.data.highestPriceBrandList[*].brandName").exists())
            .andExpect(jsonPath("$.data.highestPriceBrandList[*].price").exists());
    }

    @DisplayName("카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격 조회 - 카테고리별 최저가격 브랜드가 없는 경우")
    @Test
    void getLowestAndHighestPriceBrandByCategory_noData_returnSuccess() throws Exception {
        // given
        Category category = Category.TOP;

        given(
            productQueryService.getLowestPriceAllProductByCategory(any(Category.class))).willReturn(
            List.of());
        given(productQueryService.getHighestPriceAllProductByCategory(
            any(Category.class))).willReturn(List.of());

        // when
        ResultActions resultActions = requestLowestAndHighestBrandByCategory(category.name());

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.data.category").value(category.name()))
            .andExpect(jsonPath("$.data.lowestPriceBrandList", hasSize(0)))
            .andExpect(jsonPath("$.data.highestPriceBrandList", hasSize(0)));
    }

    @DisplayName("카테고리 이름으로 최저, 최고 가격 브랜드와 상품 가격 조회 - 유효하지 않은 카테고리인 경우")
    @Test
    void getLowestAndHighestPriceBrandByCategory_invalidCategory_returnBadRequest()
        throws Exception {
        // given
        String invalidCategory = "INVALID";

        // when
        ResultActions resultActions = requestLowestAndHighestBrandByCategory(invalidCategory);

        // then
        resultActions.andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.resultCode").value("FAIL"))
            .andExpect(jsonPath("$.data").value(INVALID_CATEGORY + invalidCategory));
    }

    @DisplayName("최저가격에 판매하는 브랜드와 카테고리별 상품 가격 조회")
    @Test
    void getLowestPriceBrand_returnSuccess() throws Exception {
        // given
        String brandName = "BrandA";
        given(productQueryService.getLowestPriceBrand()).willReturn(MockData.getLowestPriceBrand(brandName));

        // when
        ResultActions resultActions = requestLowestPriceBrand();

        // then
        resultActions.andExpect(status().isOk())
            .andExpect(jsonPath("$.data.lowestBrandInfo.brandName").value(brandName))
            .andExpect(jsonPath("$.data.lowestBrandInfo.categoryPriceInfoList", hasSize(8)))
            .andExpect(jsonPath("$.data.lowestBrandInfo.categoryPriceInfoList[*].category").exists())
            .andExpect(jsonPath("$.data.lowestBrandInfo.categoryPriceInfoList[*].price").exists())
            .andExpect(jsonPath("$.data.lowestBrandInfo.totalPrice").value("36,000"));
    }

    private ResultActions requestLowestPriceBrand() throws Exception {
        return mvc.perform(get("/api/v1/products/lowest-price-brand")
            .contentType(MediaType.APPLICATION_JSON));
    }

    private ResultActions requestLowestAndHighestBrandByCategory(String category)
        throws Exception {
        return mvc.perform(
            get("/api/v1/products/categories/{category}/prices/lowest-and-highest", category)
                .contentType(MediaType.APPLICATION_JSON));
    }


    private ResultActions requestLowestBrandByCategory() throws Exception {
        return mvc.perform(get("/api/v1/products/categories/prices/lowest")
            .contentType(MediaType.APPLICATION_JSON));
    }
}