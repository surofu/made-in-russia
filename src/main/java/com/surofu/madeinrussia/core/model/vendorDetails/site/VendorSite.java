package com.surofu.madeinrussia.core.model.vendorDetails.site;

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
@Table(name = "vendor_details_sites")
public final class VendorSite implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_details_id")
    private VendorDetails vendorDetails;

    @Embedded
    private VendorSiteUrl url;

    @Embedded
    private VendorSiteCreationDate creationDate;

    @Embedded
    private VendorSiteLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        return id != null && id.equals(((VendorSite) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
