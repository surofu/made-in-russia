package com.surofu.exporteru.core.model.vendorDetails.phoneNumber;

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
public final class VendorPhoneNumberPhoneNumber implements Serializable {
  @Column(name = "phone_number", nullable = false)
  private String value;

  public VendorPhoneNumberPhoneNumber(String phoneNumber) {
    if (phoneNumber == null || StringUtils.trimToNull(phoneNumber) == null) {
      throw new LocalizedValidationException("validation.phone_number.empty");
    }
    if (phoneNumber.length() < 7) {
      throw new LocalizedValidationException("validation.phone_number.min_length");
    }
    if (phoneNumber.length() > 255) {
      throw new LocalizedValidationException("validation.phone_number.max_length");
    }
    this.value = phoneNumber;
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof VendorPhoneNumberPhoneNumber that)) {
      return false;
    }
    return Objects.equals(value, that.value);
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(value);
  }
}
