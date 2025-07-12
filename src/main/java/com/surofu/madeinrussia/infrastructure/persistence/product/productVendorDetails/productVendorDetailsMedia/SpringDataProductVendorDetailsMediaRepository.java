package com.surofu.madeinrussia.infrastructure.persistence.product.productVendorDetails.productVendorDetailsMedia;

import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMedia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataProductVendorDetailsMediaRepository extends JpaRepository<ProductVendorDetailsMedia, Long> {

    @Query("""
    select
    m.id as id,
    m.image as image,
    m.mediaType as mediaType,
    m.position as position,
    m.creationDate as creationDate,
    m.lastModificationDate as lastModificationDate
    from ProductVendorDetailsMedia m
    where m.productVendorDetails.id = :id
    order by m.position.value
    """)
    List<ProductVendorDetailsMediaView> findAllViewsByProductVendorDetails_Id(@Param("id") Long id);
}
