package com.surofu.exporteru.core.model.vendorDetails.country;

import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
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
@Table(
        name = "vendor_countries",
        indexes = {
                @Index(
                        name = "idx_vendor_countries_vendor_details_id",
                        columnList = "vendor_details_id"
                )
        }
)
public final class VendorCountry implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "vendor_details_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_vendor_countries_vendor_details_id")
    )
    private VendorDetails vendorDetails;

    @Embedded
    private VendorCountryName name;

    @Embedded
    private VendorCountryCreationDate creationDate;

    @Embedded
    private VendorCountryLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VendorCountry)) return false;
        return id != null && id.equals(((VendorCountry) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
