package com.surofu.madeinrussia.core.model.vendorDetails.view;

import com.surofu.madeinrussia.core.model.user.User;
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
        name = "vendor_views",
        indexes = {
                @Index(name = "idx_vendor_views_vendor_details_id", columnList = "vendor_details_id"),
                @Index(name = "idx_vendor_views_user_id", columnList = "user_id")
        }
)
public final class VendorView implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "vendor_details_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_views_vendor_details_id")
    )
    private VendorDetails vendorDetails;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_views_user_id")
    )
    private User user;

    @Embedded
    private VendorViewCreationDate creationDate;

    @Embedded
    private VendorViewLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VendorView)) return false;
        return id != null && id.equals(((VendorView) o).id);
    }
}
