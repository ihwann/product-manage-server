package com.musinsa.productmanageserver.mock;

import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.product.dto.internal.BrandInfo;
import com.musinsa.productmanageserver.product.dto.internal.BrandInsertDto;
import com.musinsa.productmanageserver.product.dto.internal.BrandUpdateDto;
import com.musinsa.productmanageserver.product.dto.internal.CategoryPriceInfo;
import com.musinsa.productmanageserver.product.dto.internal.LowestBrandInfo;
import com.musinsa.productmanageserver.product.dto.internal.ProductInfo;
import com.musinsa.productmanageserver.product.dto.internal.ProductInsertDto;
import com.musinsa.productmanageserver.product.dto.internal.ProductUpdateDto;
import com.musinsa.productmanageserver.product.model.BrandEntity;
import com.musinsa.productmanageserver.product.model.ProductEntity;
import java.util.List;

public class MockData {

    public static BrandInfo getBrandInfo() {
        return BrandInfo.builder()
            .brandId(1L)
            .brandName("BrandA")
            .build();
    }

    public static BrandInfo getBrandInfo(Long brandId, String brandName) {
        return BrandInfo.builder()
            .brandId(brandId)
            .brandName(brandName)
            .build();
    }

    public static ProductInfo getProductInfo() {
        return ProductInfo.builder()
            .productId(1L)
            .brandInfo(getBrandInfo())
            .category(Category.TOP)
            .productName("ProductA")
            .productPrice(10000)
            .build();
    }

    public static ProductInfo getProductInfo(Long productId, BrandInfo brandInfo, Category category,
        String productName, Integer productPrice) {
        return ProductInfo.builder()
            .productId(productId)
            .brandInfo(brandInfo)
            .category(category)
            .productName(productName)
            .productPrice(productPrice)
            .build();
    }

    public static ProductUpdateDto getProductUpdateDto() {
        return ProductUpdateDto.builder()
            .productId(1L)
            .productPrice(20000)
            .build();
    }

    public static ProductEntity getProductEntity() {
        return ProductEntity.newBuilder()
            .insertDto(getProductInsertDto())
            .brandEntity(getBrandEntity(""))
            .build();
    }

    public static BrandEntity getBrandEntity(String brandName) {
        BrandInsertDto brandInsertDto = getBrandInsertDto(
            brandName);

        return BrandEntity.newBuilder()
            .insertDto(brandInsertDto)
            .build();
    }

    public static BrandInsertDto getBrandInsertDto(String brandName) {
        return BrandInsertDto.builder()
            .brandName(brandName)
            .build();
    }

    public static BrandUpdateDto getBrandUpdateDto(String brandName) {
        return BrandUpdateDto.builder()
            .brandId(1L)
            .brandName(brandName)
            .build();
    }

    public static ProductInsertDto getProductInsertDto() {
        return ProductInsertDto.builder()
            .brandId(1L)
            .category(Category.TOP)
            .productName("ProductA")
            .productPrice(10000)
            .build();
    }

    public static List<ProductInfo> getProductInfoList() {
        return List.of(
            getProductInfo(1L, getBrandInfo(), Category.TOP, "상의", 1000),
            getProductInfo(2L, getBrandInfo(), Category.BOTTOM, "하의", 2000),
            getProductInfo(3L, getBrandInfo(), Category.OUTER, "아우터", 3000),
            getProductInfo(4L, getBrandInfo(), Category.SNEAKERS, "스니커즈", 4000),
            getProductInfo(5L, getBrandInfo(), Category.HAT, "모자", 5000),
            getProductInfo(6L, getBrandInfo(), Category.SOCKS, "양말", 6000),
            getProductInfo(7L, getBrandInfo(), Category.BAG, "가방", 7000),
            getProductInfo(8L, getBrandInfo(), Category.ACCESSORY, "액세서리", 8000)
        );
    }

    public static List<ProductInfo> getLowestPriceAllProductByCategory(Category category) {
        return List.of(
            getProductInfo(1L, getBrandInfo(1L, "브랜드A"), category, "A상품", 1000),
            getProductInfo(2L, getBrandInfo(2L, "브랜드B"), category, "B상품", 1000)
        );
    }

    public static List<ProductInfo> getHighestPriceAllProductByCategory(Category category) {
        return List.of(
            getProductInfo(3L, getBrandInfo(3L, "브랜드C"), category, "C상품", 5000),
            getProductInfo(4L, getBrandInfo(4L, "브랜드D"), category, "D상품", 5000)
        );
    }

    public static List<CategoryPriceInfo> getCategoryPriceInfoList() {
        return getProductInfoList().stream()
            .map(productInfo -> CategoryPriceInfo.fromProductInfoBuilder()
                .productInfo(productInfo)
                .build())
            .toList();
    }

    public static LowestBrandInfo getLowestPriceBrand(String brandName) {

        return LowestBrandInfo.ofBuilder()
            .brandName(brandName)
            .categoryPriceInfoList(getCategoryPriceInfoList())
            .totalPrice(36000)
            .build();
    }
}
