package com.surofu.exporteru.core.model.vendorDetails.media;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VendorMediaPosition implements Serializable {
  @Column(name = "position", nullable = false)
  private Integer value = 0;

  public VendorMediaPosition(Integer position) {
    this.value = Objects.requireNonNullElse(position, 0);
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof VendorMediaPosition that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
