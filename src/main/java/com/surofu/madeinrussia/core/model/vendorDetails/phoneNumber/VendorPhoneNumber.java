package com.surofu.madeinrussia.core.model.vendorDetails.phoneNumber;

import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vendor_details_phone_numbers")
public final class VendorPhoneNumber implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_details_id")
    private VendorDetails vendorDetails;

    @Embedded
    private VendorPhoneNumberPhoneNumber phoneNumber;

    @Embedded
    private VendorPhoneNumberCreationDate creationDate;

    @Embedded
    private VendorPhoneNumberLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id.equals(((VendorPhoneNumber) o).id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
