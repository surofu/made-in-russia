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
public class VendorMediaMimeType implements Serializable {
  @Column(name = "mime_type", nullable = false)
  private String value;

  public VendorMediaMimeType(String type) {
    if (StringUtils.trimToNull(type) == null) {
      throw new LocalizedValidationException("validation.media.mime_type.empty");
    }
    if (type.trim().length() > 255) {
      throw new LocalizedValidationException("validation.media.mime_type.max_length");
    }
    this.value = type.trim();
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof VendorMediaMimeType that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
