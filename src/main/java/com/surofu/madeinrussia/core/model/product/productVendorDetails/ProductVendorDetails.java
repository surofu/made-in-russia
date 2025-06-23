package com.surofu.madeinrussia.core.model.product.productVendorDetails;


import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.model.product.productVendorDetails.productVendorDetailsMedia.ProductVendorDetailsMedia;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "product_vendor_details")
public final class ProductVendorDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @OneToMany(
            mappedBy = "productVendorDetails",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ProductVendorDetailsMedia> media = new HashSet<>();

    @Embedded
    private ProductVendorDetailsDescription description;

    @Embedded
    private ProductVendorDetailsCreationDate creationDate;

    @Embedded
    private ProductVendorDetailsLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ProductVendorDetails)) return false;
        return id != null && id.equals(((ProductVendorDetails) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
