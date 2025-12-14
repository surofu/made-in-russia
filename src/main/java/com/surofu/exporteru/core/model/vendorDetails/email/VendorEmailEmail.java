package com.surofu.exporteru.core.model.vendorDetails.email;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Transient;
import java.io.Serializable;
import java.util.Objects;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.Generated;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;

@Generated
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class VendorEmailEmail implements Serializable {
  @Transient
  private static final Pattern EMAIL_PATTERN =
      Pattern.compile("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$");
  @Column(name = "email", nullable = false)
  private String value;

  public VendorEmailEmail(String value) {
    if (value == null || StringUtils.trimToNull(value) == null) {
      throw new LocalizedValidationException("validation.email.empty");
    }
    if (value.length() > 255) {
      throw new LocalizedValidationException("validation.email.length");
    }
    if (!EMAIL_PATTERN.matcher(value).matches()) {
      throw new LocalizedValidationException("validation.email.format");
    }
    this.value = value.toLowerCase();
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof VendorEmailEmail that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
