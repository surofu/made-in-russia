package com.surofu.exporteru.infrastructure.persistence.vendor.country;

import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountry;
import com.surofu.exporteru.core.repository.VendorCountryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class JpaVendorCountryRepository implements VendorCountryRepository {

    private final SpringDataVendorCountryRepository repository;

    @Override
    public List<VendorCountryView> getAllViewsByVendorDetailsIdAndLang(Long id, String lang) {
        return repository.findAllViewsByVendorDetailsIdAndLang(id, lang);
    }

    @Override
    public List<VendorCountry> getAllByVendorDetailsId(Long id) {
        return repository.findAllByVendorDetailsId(id);
    }

    @Override
    public List<VendorCountry> getByVendorId(Long vendorId) {
        return repository.findByVendorDetailsId(vendorId);
    }

    @Override
    public void saveAll(Collection<VendorCountry> vendorCountries) {
        repository.saveAll(vendorCountries);
    }

    @Override
    public void deleteAll(Collection<VendorCountry> vendorSites) {
        repository.deleteAll(vendorSites);
    }

    @Override
    public void flush() {
        repository.flush();
    }
}
