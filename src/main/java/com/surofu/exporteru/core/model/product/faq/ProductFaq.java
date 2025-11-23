package com.surofu.exporteru.core.model.product.faq;

import com.surofu.exporteru.core.model.product.Product;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.io.Serializable;
import java.util.Objects;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "product_faq",
    indexes = {
        @Index(
            name = "idx_product_faq_product_id",
            columnList = "product_id"
        )
    }
)
public final class ProductFaq implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ToString.Exclude
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(
      name = "product_id",
      nullable = false,
      foreignKey = @ForeignKey(name = "fk_product_faq_product_id")
  )
  private Product product;

  @Embedded
  private ProductFaqQuestion question;

  @Embedded
  private ProductFaqAnswer answer;

  @Embedded
  private ProductFaqCreationDate creationDate;

  @Embedded
  private ProductFaqLastModificationDate lastModificationDate;

  @Override
  public boolean equals(Object o) {
      if (this == o) {
          return true;
      }
      if (!(o instanceof ProductFaq productFaq)) {
          return false;
      }
    return Objects.equals(question, productFaq.question) &&
        Objects.equals(answer, productFaq.answer);
  }

  @Override
  public int hashCode() {
    return Objects.hash(question, answer);
  }
}
