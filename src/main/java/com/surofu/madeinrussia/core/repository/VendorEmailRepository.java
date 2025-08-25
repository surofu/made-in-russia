package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.vendorDetails.email.VendorEmail;

import java.util.List;

public interface VendorEmailRepository {
    List<VendorEmail> getAllByVendorDetailsId(Long id);
}
