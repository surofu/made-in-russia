package com.surofu.madeinrussia.infrastructure.persistence.vendor.media;

import com.surofu.madeinrussia.core.model.vendorDetails.media.VendorMedia;
import com.surofu.madeinrussia.core.repository.VendorMediaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaVendorMediaRepository implements VendorMediaRepository {

    private final SpringDataVendorMediaRepository repository;

    @Override
    public List<VendorMedia> getAllByVendorDetailsId(Long vendorDetailsId) {
        return repository.findAllByVendorDetailsIdOrderByPositionAsc(vendorDetailsId);
    }

    @Override
    public Optional<VendorMedia> getById(Long id) {
        return repository.findById(id);
    }

    @Override
    public void saveAll(List<VendorMedia> vendorMediaList) {
        repository.saveAll(vendorMediaList);
    }

    @Override
    public void delete(VendorMedia vendorMedia) {
        repository.delete(vendorMedia);
    }

    @Override
    public void deleteAll(List<VendorMedia> vendorMediaList) {
        repository.deleteAll(vendorMediaList);
    }

    @Override
    public void flush() {
        repository.flush();
    }
}
