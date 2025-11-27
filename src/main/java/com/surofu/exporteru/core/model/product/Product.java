package com.surofu.exporteru.core.model.product;

import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTerm;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.product.characteristic.ProductCharacteristic;
import com.surofu.exporteru.core.model.product.deliveryMethodDetails.ProductDeliveryMethodDetails;
import com.surofu.exporteru.core.model.product.faq.ProductFaq;
import com.surofu.exporteru.core.model.product.media.ProductMedia;
import com.surofu.exporteru.core.model.product.packageOption.ProductPackageOption;
import com.surofu.exporteru.core.model.product.price.ProductPrice;
import com.surofu.exporteru.core.model.product.review.ProductReview;
import com.surofu.exporteru.core.model.product.review.media.ProductReviewMedia;
import com.surofu.exporteru.core.model.user.User;
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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OrderBy;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Formula;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "products")
public final class Product implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @Enumerated(EnumType.STRING)
  @Column(name = "approve_status", nullable = false)
  private ApproveStatus approveStatus = ApproveStatus.PENDING;

  @ManyToOne(fetch = FetchType.EAGER)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @Fetch(FetchMode.SUBSELECT)
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "products_delivery_methods",
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "delivery_method_id")
  )
  private Set<DeliveryMethod> deliveryMethods = new HashSet<>();

  @OneToMany(
      mappedBy = "product",
      fetch = FetchType.LAZY,
      orphanRemoval = true
  )
  @OrderBy("position")
  private Set<ProductMedia> media = new HashSet<>();

  @Fetch(FetchMode.SUBSELECT)
  @ManyToMany(fetch = FetchType.EAGER)
  @JoinTable(
      name = "similar_products",
      joinColumns = @JoinColumn(name = "parent_product_id"),
      inverseJoinColumns = @JoinColumn(name = "similar_product_id")
  )
  private Set<Product> similarProducts = new HashSet<>();

  @OneToMany(
      mappedBy = "product",
      fetch = FetchType.LAZY,
      orphanRemoval = true
  )
  @OrderBy("creationDate")
  private Set<ProductCharacteristic> characteristics = new HashSet<>();

  @OneToMany(
      mappedBy = "product",
      fetch = FetchType.LAZY,
      orphanRemoval = true
  )
  @OrderBy("creationDate")
  private Set<ProductReview> reviews = new HashSet<>();

  @Transient
  private Set<ProductReviewMedia> reviewsMedia = new HashSet<>();

  @OneToMany(
      mappedBy = "product",
      fetch = FetchType.LAZY,
      orphanRemoval = true
  )
  @OrderBy("creationDate")
  private Set<ProductFaq> faq = new HashSet<>();

  @OneToMany(
      mappedBy = "product",
      fetch = FetchType.LAZY,
      orphanRemoval = true
  )
  @OrderBy("creationDate")
  private Set<ProductPrice> prices = new HashSet<>();

  @OneToMany(
      mappedBy = "product",
      fetch = FetchType.LAZY,
      orphanRemoval = true
  )
  @OrderBy("creationDate")
  private Set<ProductDeliveryMethodDetails> deliveryMethodDetails = new HashSet<>();

  @Fetch(FetchMode.JOIN)
  @OneToMany(
      mappedBy = "product",
      fetch = FetchType.LAZY,
      orphanRemoval = true
  )
  @OrderBy("creationDate")
  private Set<ProductPackageOption> packageOptions = new HashSet<>();

  @Fetch(FetchMode.SUBSELECT)
  @ManyToMany
  @JoinTable(
      name = "products_delivery_terms",
      joinColumns = @JoinColumn(name = "product_id"),
      inverseJoinColumns = @JoinColumn(name = "delivery_term_id")
  )
  private Set<DeliveryTerm> deliveryTerms = new HashSet<>();

  @Embedded
  private ProductArticleCode articleCode;

  @Embedded
  private ProductTitle title;

  @Embedded
  private ProductDescription description;

  @Transient
  private Double rating;

  @Formula("(select count(*) from product_reviews r where r.product_id = id)")
  private Integer reviewsCount;

  @Embedded
  private ProductPreviewImageUrl previewImageUrl;

  @Embedded
  private ProductCreationDate creationDate;

  @Embedded
  private ProductMinimumOrderQuantity minimumOrderQuantity;

  @Embedded
  private ProductDiscountExpirationDate discountExpirationDate;

  @Embedded
  private ProductLastModificationDate lastModificationDate;

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof Product product)) {
      return false;
    }
    return Objects.equals(id, product.id);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id);
  }
}
