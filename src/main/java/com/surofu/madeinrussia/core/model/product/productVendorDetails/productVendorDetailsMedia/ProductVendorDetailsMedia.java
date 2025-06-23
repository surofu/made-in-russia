package com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia;

import com.surofu.madeinrussia.core.model.product.productVendorDetails.ProductVendorDetails;
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
@Table(name = "product_vendor_details_media")
public final class ProductVendorDetailsMedia implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_vendor_details_id", nullable = false)
    private ProductVendorDetails productVendorDetails;

    @Embedded
    private ProductVendorDetailsMediaImage image;

    @Embedded
    private ProductVendorDetailsMediaCreationDate creationDate;

    @Embedded
    private ProductVendorDetailsMediaLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVendorDetailsMedia)) return false;
        return id != null && id.equals(((ProductVendorDetailsMedia) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
