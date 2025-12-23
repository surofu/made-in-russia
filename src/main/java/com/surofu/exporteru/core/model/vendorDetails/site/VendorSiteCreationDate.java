package com.surofu.exporteru.core.model.vendorDetails.site;

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
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorSiteCreationDate implements Serializable {
  @CreationTimestamp
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "creation_date", nullable = false, updatable = false, columnDefinition = "timestamptz default now()")
  private ZonedDateTime value;

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof VendorSiteCreationDate that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
