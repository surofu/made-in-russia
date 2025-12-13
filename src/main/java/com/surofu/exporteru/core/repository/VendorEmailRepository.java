package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmail;

import java.util.Collection;
import java.util.List;

public interface VendorEmailRepository {
    List<VendorEmail> getAllByVendorDetailsId(Long id);

    void saveAll(Collection<VendorEmail> vendorEmails);

    void deleteAll(Collection<VendorEmail> vendorEmails);

    void flush();
}
