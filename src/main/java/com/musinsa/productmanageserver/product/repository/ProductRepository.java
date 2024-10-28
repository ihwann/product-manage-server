package com.musinsa.productmanageserver.product.repository;

import com.musinsa.productmanageserver.product.model.ProductEntity;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<ProductEntity, Long> {
    Optional<ProductEntity> findById(Long productId);

    @Query("SELECT p FROM ProductEntity p JOIN FETCH p.brand WHERE p.id = :id")
    Optional<ProductEntity> findByIdWithBrand(@Param("id") Long id);

    @Query("SELECT p FROM ProductEntity p JOIN FETCH p.brand")
    List<ProductEntity> findAllWithBrand();

}
