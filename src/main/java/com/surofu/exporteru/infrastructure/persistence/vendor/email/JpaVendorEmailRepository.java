package com.surofu.exporteru.infrastructure.persistence.vendor.email;

import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmail;
import com.surofu.exporteru.core.repository.VendorEmailRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaVendorEmailRepository implements VendorEmailRepository {

    private final SpringDataVendorEmailRepository repository;

    @Override
    public List<VendorEmail> getAllByVendorDetailsId(Long id) {
        return repository.getAllByVendorDetails_Id(id);
    }

    @Override
    public void saveAll(Collection<VendorEmail> vendorEmails) {
        repository.saveAll(vendorEmails);
    }

    @Override
    public void deleteAll(Collection<VendorEmail> vendorEmails) {
        repository.deleteAll(vendorEmails);
    }

    @Override
    public void flush() {
        repository.flush();
    }
}
