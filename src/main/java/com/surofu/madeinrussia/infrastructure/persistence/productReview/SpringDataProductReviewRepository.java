package com.surofu.madeinrussia.infrastructure.persistence.productReview;

import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProductReviewRepository extends JpaRepository<ProductReview, Long>, JpaSpecificationExecutor<ProductReview> {

    Page<ProductReview> findAll(Specification<ProductReview> spec, Pageable pageable);

    @Query("""
            SELECT DISTINCT pr FROM ProductReview pr
            LEFT JOIN FETCH pr.media m
            LEFT JOIN FETCH pr.user u
            LEFT JOIN FETCH u.vendorDetails vd
            LEFT JOIN FETCH vd.vendorCountries
            LEFT JOIN FETCH vd.vendorProductCategories
            LEFT JOIN FETCH pr.product p
            LEFT JOIN FETCH p.category c
            WHERE pr.id IN :ids
            ORDER BY pr.lastModificationDate.value DESC
            """)
    List<ProductReview> findByIdInWithMedia(@Param("ids") List<Long> ids);

    @Query("""
            select avg(r.rating.value) from ProductReview r
            where r.product.user.id = :productUserId
            and r.product.user.role = 'ROLE_VENDOR'
            """)
    Double findAverageRatingByProductVendorId(@Param("productUserId") Long productUserId);
}
