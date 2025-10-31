package com.surofu.exporteru.core.model.vendorDetails.email;

import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "vendor_details_emails")
public final class VendorEmail implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vendor_details_id")
    private VendorDetails vendorDetails;

    @Embedded
    private VendorEmailEmail email;

    @Embedded
    private VendorEmailCreationDate creationDate;

    @Embedded
    private VendorEmailLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VendorEmail)) return false;
        return id != null && id.equals(((VendorEmail) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
