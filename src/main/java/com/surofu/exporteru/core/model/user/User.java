package com.surofu.exporteru.core.model.user;

import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.product.review.ProductReview;
import com.surofu.exporteru.core.model.session.Session;
import com.surofu.exporteru.core.model.user.password.UserPassword;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetails;
import com.surofu.exporteru.core.model.vendorDetails.view.VendorView;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users")
public final class User implements Serializable {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, columnDefinition = "user_role")
  private UserRole role = UserRole.ROLE_USER;
  @Embedded
  private UserIsEnabled isEnabled = new UserIsEnabled(true);
  @Column(name = "telegram_user_id")
  private Long telegramUserId;

  @ToString.Exclude
  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "user",
      orphanRemoval = true
  )
  private Set<VendorView> views = new HashSet<>();

  @ToString.Exclude
  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "user",
      orphanRemoval = true
  )
  private Set<Product> products = new HashSet<>();

  @ToString.Exclude
  @ManyToMany
  @JoinTable(
      name = "users_favorite_products",
      joinColumns = @JoinColumn(name = "user_id"),
      inverseJoinColumns = @JoinColumn(name = "product_id")
  )
  private Set<Product> favoriteProducts = new HashSet<>();

  @ToString.Exclude
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "user",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE},
      orphanRemoval = true
  )
  private Set<UserPassword> password = new HashSet<>();

  @ToString.Exclude
  @Getter(AccessLevel.NONE)
  @Setter(AccessLevel.NONE)
  @OneToMany(
      fetch = FetchType.LAZY,
      mappedBy = "user",
      cascade = {CascadeType.PERSIST, CascadeType.MERGE},
      orphanRemoval = true
  )
  private Set<VendorDetails> vendorDetails = new HashSet<>();

  @ToString.Exclude
  @OneToMany(
      mappedBy = "user",
      fetch = FetchType.LAZY,
      orphanRemoval = true
  )
  private Set<ProductReview> productReviews = new HashSet<>();

  @ToString.Exclude
  @OneToMany(
      mappedBy = "user",
      fetch = FetchType.LAZY,
      cascade = {CascadeType.PERSIST, CascadeType.MERGE},
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
    if (password == null || password.isEmpty()) {
      return null;
    }
    return password.iterator().next();
  }

  public void setPassword(UserPassword password) {
    this.password = new HashSet<>();
    password.setUser(this);
    this.password.add(password);
  }

  public VendorDetails getVendorDetails() {
    if (vendorDetails == null || vendorDetails.isEmpty()) {
      return null;
    }
    return vendorDetails.iterator().next();
  }

  public void setVendorDetails(VendorDetails vendorDetails) {
    if (this.vendorDetails == null) {
      this.vendorDetails = new HashSet<>();
    } else {
      this.vendorDetails.clear();
    }
    if (vendorDetails != null) {
      vendorDetails.setUser(this);
      this.vendorDetails.add(vendorDetails);
    }
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof User user)) {
      return false;
    }
    if (id == null || user.id == null) {
      return false;
    }
    return Objects.equals(id, user.id);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
