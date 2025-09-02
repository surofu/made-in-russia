package com.surofu.madeinrussia.infrastructure.persistence.vendor.phoneNumber;

import com.surofu.madeinrussia.core.model.vendorDetails.phoneNumber.VendorPhoneNumber;
import com.surofu.madeinrussia.core.repository.VendorPhoneNumberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaVendorPhoneNumberRepository
        implements VendorPhoneNumberRepository {

    private final SpringDataVendorPhoneNumberRepository repository;

    @Override
    public List<VendorPhoneNumber> getAllByVendorDetailsId(Long vendorDetailsId) {
        return repository.findAllByVendorDetails_Id(vendorDetailsId);
    }

    @Override
    public void saveAll(Collection<VendorPhoneNumber> vendorPhoneNumbers) {
        repository.saveAll(vendorPhoneNumbers);
    }

    @Override
    public void deleteAll(Collection<VendorPhoneNumber> vendorPhoneNumbers) {
        repository.deleteAll(vendorPhoneNumbers);
    }

    @Override
    public void flush() {
        repository.flush();
    }
}
