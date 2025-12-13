package com.surofu.exporteru.core.model.vendorDetails.faq;

import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "vendor_faq",
        indexes = {
                @Index(name = "idx_vendor_faq_vendor_details_id", columnList = "vendor_details_id")
        }
)
public final class VendorFaq {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "vendor_details_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_vendor_faq_vendor_details_id")
    )
    private VendorDetails vendorDetails;

    @Embedded
    private VendorFaqQuestion question;

    @Embedded
    private VendorFaqAnswer answer;

    @Embedded
    private VendorFaqCreationDate creationDate;

    @Embedded
    private VendorFaqLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VendorFaq)) return false;
        return id != null && id.equals(((VendorFaq) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
