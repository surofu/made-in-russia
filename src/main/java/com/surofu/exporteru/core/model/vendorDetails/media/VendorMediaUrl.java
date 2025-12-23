package com.surofu.exporteru.core.model.vendorDetails.media;

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
public class VendorMediaUrl implements Serializable {
  @Column(name = "url", nullable = false)
  private String value;

  public VendorMediaUrl(String url) {
    if (StringUtils.trimToNull(url) == null) {
      throw new LocalizedValidationException("validation.media.url.empty");
    }
    if (url.trim().length() > 20_000) {
      throw new LocalizedValidationException("validation.media.url.max_length");
    }
    this.value = url.trim();
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof VendorMediaUrl that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
