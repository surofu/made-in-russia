package com.surofu.exporteru.infrastructure.persistence.vendor.faq;

import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataVendorFaqRepository extends JpaRepository<VendorFaq, Long> {

    @Query(value = """
            select
            id,
            coalesce(
                question_translations -> :lang,
                question
            ) as question,
            coalesce(
                answer_translations -> :lang,
                answer
            ) as answer,
            creation_date,
            last_modification_date
            from vendor_faq
            where vendor_details_id = :id
            """, nativeQuery = true)
    List<VendorFaqView> findAllViewsByVendorDetailsIdAndLang(@Param("id") Long id, @Param("lang") String lang);

    Optional<VendorFaq> findByIdAndVendorDetailsUserId(Long faqId, Long vendorId);

    Optional<VendorFaq> findByIdAndVendorDetails_Id(Long id, Long vendorDetailsId);
}
