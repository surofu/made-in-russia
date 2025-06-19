package com.surofu.madeinrussia.core.model.vendorDetails.vendorView;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "vendor_views",
        indexes = {
                @Index(name = "idx_vendor_views_vendor_details_id", columnList = "vendorDetails"),
                @Index(name = "idx_vendor_views_user_id", columnList = "user")
        }
)
public final class VendorView implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

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
}
