package com.surofu.madeinrussia.core.model.vendorDetails;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorDetailsCompanyName implements Serializable {

    @Column(name = "company_name", nullable = false, unique = true)
    private String value;

    private VendorDetailsCompanyName(String companyName) {
        if (companyName == null || companyName.trim().isEmpty()) {
            throw new IllegalArgumentException("Название компании продавца не может быть пустым");
        }
        
        this.value = companyName;
    }

    public static VendorDetailsCompanyName of(String companyName) {
        return new VendorDetailsCompanyName(companyName);
    }

    @Override
    public String toString() {
        return value;
    }
}
