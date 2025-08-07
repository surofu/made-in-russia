package com.surofu.madeinrussia.core.model.vendorDetails;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountry;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaq;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategory;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorView.VendorView;
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
@Table(
        name = "vendor_details",
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "unique_vendor_details_inn",
                        columnNames = "inn"
                ),
                @UniqueConstraint(
                        name = "unique_vendor_details_company_name",
                        columnNames = "company_name"
                )
        },
        indexes = {
                @Index(
                        name = "idx_vendor_details_user_id",
                        columnList = "user_id"
                )
        }
)
public final class VendorDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_vendor_details_user_id")
    )
    private User user;

    @Embedded
    private VendorDetailsInn inn;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<VendorCountry> vendorCountries = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<VendorProductCategory> vendorProductCategories = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<VendorFaq> faq = new HashSet<>();

    @ToString.Exclude
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<VendorView> vendorViews = new HashSet<>();

    @Transient
    private Long vendorViewsCount = 0L;

    @Embedded
    private VendorDetailsCreationDate creationDate;

    @Embedded
    private VendorDetailsLastModificationDate lastModificationDate;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof VendorDetails)) return false;
        return id != null && id.equals(((VendorDetails) o).id);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
