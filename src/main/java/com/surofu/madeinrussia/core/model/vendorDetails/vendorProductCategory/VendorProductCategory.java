package com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory;

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
@Table(
        name = "vendor_product_categories",
        indexes = {
                @Index(
                        name = "idx_vendor_product_categories_vendor_details_id",
                        columnList = "vendor_details_id"
                )
        }
)
public final class VendorProductCategory implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "vendor_details_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_vendor_product_categories_vendor_details_id")
    )
    private VendorDetails vendorDetails;

    @Embedded
    private VendorProductCategoryName name;

    @Embedded
    private VendorProductCategoryCreationDate creationDate;

    @Embedded
    private VendorProductCategoryLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VendorProductCategory)) return false;
        return id != null && id.equals(((VendorProductCategory) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
