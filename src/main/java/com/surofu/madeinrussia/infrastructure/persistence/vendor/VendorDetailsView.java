package com.surofu.madeinrussia.infrastructure.persistence.vendor;

import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsCreationDate;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsLastModificationDate;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.country.VendorCountryView;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.faq.VendorFaqView;
import com.surofu.madeinrussia.infrastructure.persistence.vendor.productCategory.VendorProductCategoryView;
import org.springframework.beans.factory.annotation.Value;

import java.util.List;

public interface VendorDetailsView {
    Long getId();

    VendorDetailsInn getInn();

    @Value("#{@jpaVendorDetailsRepository.getViewsCountById(target.id)}")
    Long getViewsCount();

    VendorDetailsCreationDate getCreationDate();

    VendorDetailsLastModificationDate getLastModificationDate();

    // External

    @Value("#{@jpaVendorFaqRepository.getAllViewsByVendorDetailsId(target.id)}")
    List<VendorFaqView> getFaqs();

    @Value("#{@jpaVendorCountryRepository.getAllViewsByVendorDetailsId(target.id)}")
    List<VendorCountryView> getCountries();

    @Value("#{@jpaVendorProductCountryRepository.getAllViewsByVendorDetailsId(target.id)}")
    List<VendorProductCategoryView> getProductCategories();
}
