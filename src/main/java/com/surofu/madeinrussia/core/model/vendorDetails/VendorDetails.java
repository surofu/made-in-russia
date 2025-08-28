package com.surofu.madeinrussia.core.model.vendorDetails;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.vendorDetails.country.VendorCountry;
import com.surofu.madeinrussia.core.model.vendorDetails.email.VendorEmail;
import com.surofu.madeinrussia.core.model.vendorDetails.faq.VendorFaq;
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
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
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
            mappedBy = "vendorDetails",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<VendorPhoneNumber> phoneNumbers = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<VendorEmail> emails = new HashSet<>();

    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "vendorDetails",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<VendorSite> sites = new HashSet<>();

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

    public void setPhoneNumbers(Collection<VendorPhoneNumber> phoneNumbers) {
        this.phoneNumbers.clear();
        if (phoneNumbers != null) {
            this.phoneNumbers.addAll(phoneNumbers);
        }
    }

    public void setEmails(Collection<VendorEmail> emails) {
        this.emails.clear();
        if (emails != null) {
            this.emails.addAll(emails);
        }
    }

    public void setSites(Collection<VendorSite> sites) {
        this.sites.clear();
        if (sites != null) {
            this.sites.addAll(sites);
        }
    }

    public void setVendorCountries(Collection<VendorCountry> vendorCountries) {
        this.vendorCountries.clear();
        if (vendorCountries != null) {
            this.vendorCountries.addAll(vendorCountries);
        }
    }

    public void setVendorProductCategories(Collection<VendorProductCategory> vendorProductCategories) {
        this.vendorProductCategories.clear();
        if (vendorProductCategories != null) {
            this.vendorProductCategories.addAll(vendorProductCategories);
        }
    }

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
