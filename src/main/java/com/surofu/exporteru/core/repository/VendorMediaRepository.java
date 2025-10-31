package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.vendorDetails.media.VendorMedia;

import java.util.List;
import java.util.Optional;

public interface VendorMediaRepository {
    List<VendorMedia> getAllByVendorDetailsId(Long vendorId);

    Optional<VendorMedia> getById(Long id);

    void saveAll(List<VendorMedia> vendorMediaList);

    void delete(VendorMedia vendorMedia);

    void deleteAll(List<VendorMedia> vendorMediaList);

    void flush();
}
