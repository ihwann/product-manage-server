package com.musinsa.productmanageserver.product.model;

import com.musinsa.productmanageserver.product.dto.internal.BrandInsertDto;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Entity
@Table(name = "brand_info")
public class BrandEntity extends BaseEntity{

    @Id
    @Column(name = "brand_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(name = "brand_name", nullable = false, length = 100)
    private String brandName;

    @Builder(builderClassName = "NewBuilder", builderMethodName = "newBuilder")
    public BrandEntity(BrandInsertDto insertDto) {
        this.brandName = insertDto.getBrandName();
    }

    public void updateBrandName(String brandName) {
        this.brandName = brandName;
    }
}
