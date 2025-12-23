package com.surofu.exporteru.core.model.advertisement;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class AdvertisementExpirationDate implements Serializable {
  @Column(name = "expiration_date")
  private ZonedDateTime value;

  public AdvertisementExpirationDate(ZonedDateTime date) {
    if (date == null) {
      throw new LocalizedValidationException("validation.advertisement.expiration_date.empty");
    }
    this.value = date;
  }

  @Override
  public String toString() {
    if (value == null) {
      return "";
    }
    return value.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof AdvertisementExpirationDate that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
