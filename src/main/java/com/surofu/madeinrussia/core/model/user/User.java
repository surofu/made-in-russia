package com.surofu.madeinrussia.core.model.user;

import com.surofu.madeinrussia.core.model.product.review.ProductReview;
import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.user.password.UserPassword;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
        name = "users",
        indexes = {
                @Index(name = "idx_users_login", columnList = "login"),
                @Index(name = "idx_users_email", columnList = "email"),
                @Index(name = "idx_users_phone_number", columnList = "phone_number"),
                @Index(name = "idx_users_vendor_details_id", columnList = "vendor_details_id")
        }
)
public final class User implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, columnDefinition = "user_role")
    private UserRole role = UserRole.ROLE_USER;

    @Embedded
    private UserIsEnabled isEnabled;

    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<UserPassword> password;

    @ToString.Exclude
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @OneToMany(
            fetch = FetchType.LAZY,
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<VendorDetails> vendorDetails = new HashSet<>();

    @ToString.Exclude
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ProductReview> productReviews = new HashSet<>();

    @ToString.Exclude
    @OneToMany(
            mappedBy = "user",
            fetch = FetchType.LAZY,
            cascade = {CascadeType.PERSIST, CascadeType.REMOVE},
            orphanRemoval = true
    )
    private Set<Session> sessions = new HashSet<>();

    @Embedded
    private UserEmail email;

    @Embedded
    private UserLogin login;

    @Embedded
    private UserPhoneNumber phoneNumber;

    @Embedded
    private UserRegion region;

    @Embedded
    private UserAvatar avatar;

    @Embedded
    private UserRegistrationDate registrationDate;

    @Embedded
    private UserLastModificationDate lastModificationDate;

    public UserPassword getPassword() {
        if (password == null) {
            return null;
        }

        return password.stream().findFirst().orElse(null);
    }

    public void setPassword(UserPassword password) {
        this.password.clear();
        if (password != null) {
            this.password.add(password);
        }
    }

    public VendorDetails getVendorDetails() {
        if (vendorDetails == null) {
            return null;
        }

        return vendorDetails.stream().findFirst().orElse(null);
    }

    public void setVendorDetails(VendorDetails vendorDetails) {
        this.vendorDetails.clear();
        if (vendorDetails != null) {
            this.vendorDetails.add(vendorDetails);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof User)) return false;
        return id != null && id.equals(((User) o).id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
