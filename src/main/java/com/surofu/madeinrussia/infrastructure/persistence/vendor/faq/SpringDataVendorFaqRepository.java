package com.surofu.madeinrussia.infrastructure.persistence.vendor.faq;

import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

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

    boolean existsByIdAndVendorDetailsUserId(Long faqId, Long vendorId);
}
