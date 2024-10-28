package com.musinsa.productmanageserver.product.model;


import com.musinsa.productmanageserver.common.type.Category;
import com.musinsa.productmanageserver.product.dto.internal.ProductInsertDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "product_info")
public class ProductEntity extends BaseEntity{

    @Id
    @Column(name = "product_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private BrandEntity brand;

    @Enumerated(EnumType.STRING)
    @Column(name = "category", nullable = false, length = 50)
    private Category category;

    @Column(name = "product_name", nullable = false, length = 100)
    private String productName;

    @Column(name = "product_price", nullable = true)
    private Integer productPrice;

    public void updatePrice(Integer productPrice) {
        this.productPrice = productPrice;
    }

    @Builder(builderClassName = "NewBuilder", builderMethodName = "NewBuilder")
    public ProductEntity(ProductInsertDto insertDto, BrandEntity brandEntity) {
        this.brand = brandEntity;
        this.category = insertDto.getCategory();
        this.productName = insertDto.getProductName();
        this.productPrice = insertDto.getProductPrice();
    }
}
