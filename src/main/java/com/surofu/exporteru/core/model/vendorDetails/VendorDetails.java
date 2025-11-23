package com.surofu.exporteru.core.model.vendorDetails;

import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountry;
import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmail;
import com.surofu.exporteru.core.model.vendorDetails.faq.VendorFaq;
import com.surofu.exporteru.core.model.vendorDetails.media.VendorMedia;
import com.surofu.exporteru.core.model.vendorDetails.phoneNumber.VendorPhoneNumber;
import com.surofu.exporteru.core.model.vendorDetails.productCategory.VendorProductCategory;
import com.surofu.exporteru.core.model.vendorDetails.site.VendorSite;
import com.surofu.exporteru.core.model.vendorDetails.view.VendorView;
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

    @ToString.Exclude
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private Set<VendorPhoneNumber> phoneNumbers = new HashSet<>();

    @ToString.Exclude
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private Set<VendorEmail> emails = new HashSet<>();

    @ToString.Exclude
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            orphanRemoval = true
    )
    private Set<VendorSite> sites = new HashSet<>();

    @ToString.Exclude
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    @OrderBy("position.value")
    private Set<VendorMedia> media = new HashSet<>();

    @ToString.Exclude
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private Set<VendorCountry> vendorCountries = new HashSet<>();

    @ToString.Exclude
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private Set<VendorProductCategory> vendorProductCategories = new HashSet<>();

    @ToString.Exclude
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private Set<VendorFaq> faq = new HashSet<>();

    @ToString.Exclude
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            cascade = {CascadeType.PERSIST, CascadeType.MERGE},
            orphanRemoval = true
    )
    private Set<VendorView> vendorViews = new HashSet<>();

    @Embedded
    private VendorDetailsInn inn;

    @Embedded
    private VendorDetailsAddress address;

    @Embedded
    private VendorDetailsDescription description = VendorDetailsDescription.of("");

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
