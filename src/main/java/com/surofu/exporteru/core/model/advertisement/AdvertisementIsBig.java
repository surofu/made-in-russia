package com.surofu.exporteru.core.model.advertisement;

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
public final class AdvertisementIsBig implements Serializable {
  @Column(name = "is_big", nullable = false, columnDefinition = "boolean default false")
  private Boolean value = Boolean.FALSE;

  public AdvertisementIsBig(Boolean state) {
    this.value = Objects.requireNonNullElse(state, Boolean.FALSE);
  }

  @Override
  public String toString() {
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AdvertisementIsBig that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
