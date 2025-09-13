package com.surofu.madeinrussia.core.model.vendorDetails;

import com.surofu.madeinrussia.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorDetailsAddress implements Serializable {

    @Column(name = "address")
    private String value;

    private VendorDetailsAddress(String address) {
        this.value = address;
    }

    public static VendorDetailsAddress of(String address) {
        return new VendorDetailsAddress(address);
    }

    @Override
    public String toString() {
        return value;
    }
}
