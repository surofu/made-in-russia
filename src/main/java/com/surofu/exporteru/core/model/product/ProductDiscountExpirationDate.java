package com.surofu.exporteru.core.model.product;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductDiscountExpirationDate implements Serializable {
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "discount_expiration_date", nullable = false, columnDefinition = "timestamptz")
  private ZonedDateTime value;

  public ProductDiscountExpirationDate(ZonedDateTime date) {
    if (date == null) {
      throw new IllegalArgumentException("Дата окончания скидки не может быть пустой");
    }
    this.value = date;
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductDiscountExpirationDate that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
