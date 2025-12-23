package com.surofu.exporteru.core.model.vendorDetails;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorDetailsInn implements Serializable {
  @Column(name = "inn", nullable = false, unique = true)
  private String value;

  public VendorDetailsInn(String inn) {
    if (StringUtils.trimToNull(inn) == null) {
      throw new LocalizedValidationException("validation.vendor.inn.empty");
    }
    if (inn.length() < 7) {
      throw new LocalizedValidationException("validation.vendor.inn.min_length");
    }
    if (inn.length() > 255) {
      throw new LocalizedValidationException("validation.vendor.inn.max_length");
    }
    this.value = inn;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof VendorDetailsInn that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
