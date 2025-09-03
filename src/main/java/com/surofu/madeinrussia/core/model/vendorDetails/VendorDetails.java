package com.surofu.madeinrussia.core.model.vendorDetails;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountry;
import com.surofu.madeinrussia.core.model.vendorDetails.email.VendorEmail;
import com.surofu.madeinrussia.core.model.vendorDetails.faq.VendorFaq;
import com.surofu.madeinrussia.core.model.vendorDetails.media.VendorMedia;
import com.surofu.madeinrussia.core.model.vendorDetails.phoneNumber.VendorPhoneNumber;
import com.surofu.madeinrussia.core.model.vendorDetails.productCategory.VendorProductCategory;
import com.surofu.madeinrussia.core.model.vendorDetails.site.VendorSite;
import com.surofu.madeinrussia.core.model.vendorDetails.view.VendorView;
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
@Table(name = "vendor_details")
public final class VendorDetails implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ToString.Exclude
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false,
            unique = true,
            foreignKey = @ForeignKey(name = "fk_vendor_details_user_id")
    )
    private User user;

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails"
    )
    private Set<VendorPhoneNumber> phoneNumbers = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails"
    )
    private Set<VendorEmail> emails = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails"
    )
    private Set<VendorSite> sites = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails"
    )
    @OrderBy("position.value")
    private Set<VendorMedia> media = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails"
    )
    private Set<VendorCountry> vendorCountries = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails"
    )
    private Set<VendorProductCategory> vendorProductCategories = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails"
    )
    private Set<VendorFaq> faq = new HashSet<>();

    @ToString.Exclude
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails"
    )
    private Set<VendorView> vendorViews = new HashSet<>();

    @Embedded
    private VendorDetailsInn inn;

    @Embedded
    private VendorDetailsDescription description;

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
