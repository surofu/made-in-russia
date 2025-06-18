package com.surofu.madeinrussia.core.model.user;

import com.surofu.madeinrussia.core.model.product.productReview.ProductReview;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetails;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
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

    @OneToOne(
            mappedBy = "user",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JoinColumn(
            name = "vendor_details_id",
            foreignKey = @ForeignKey(name = "fk_users_vendor_details_id")
    )
    private VendorDetails vendorDetails;

    @OneToMany(mappedBy = "user")
    private Set<ProductReview> productReviews = new HashSet<>();

    @Embedded
    private UserEmail email;

    @Embedded
    private UserLogin login;

    @Embedded
    private UserPhoneNumber phoneNumber;

    @Embedded
    private UserRegion region;

    @Embedded
    private UserRegistrationDate registrationDate;

    @Embedded
    private UserLastModificationDate lastModificationDate;

    public boolean hasWeekAfterRegistrationDate() {
        return ZonedDateTime.now().isAfter(registrationDate.getValue().plusWeeks(1));
    }
}
