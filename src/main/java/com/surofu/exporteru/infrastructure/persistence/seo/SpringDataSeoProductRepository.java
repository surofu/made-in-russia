package com.surofu.exporteru.infrastructure.persistence.seo;

import com.surofu.exporteru.core.model.product.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataSeoProductRepository extends JpaRepository<Product, Long> {

    @Query(value = """
            select
            p.id as id,
            p.last_modification_date as updatedAt
            from products p
            where p.approve_status = :status
            order by p.id
            """, nativeQuery = true)
    List<SeoProductView> findAllBy(@Param("status") String status);
}
