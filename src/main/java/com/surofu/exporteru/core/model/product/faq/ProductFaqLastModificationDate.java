package com.surofu.exporteru.core.model.product.faq;

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
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class ProductFaqLastModificationDate implements Serializable {
  @UpdateTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "last_modification_date", nullable = false, columnDefinition = "timestamptz default now()")
  private ZonedDateTime value = ZonedDateTime.now();

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ProductFaqLastModificationDate that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
