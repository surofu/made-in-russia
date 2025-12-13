package com.surofu.exporteru.core.model.deliveryTerm;

import com.surofu.exporteru.application.exception.LocalizedValidationException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

@Getter
@Embeddable
public final class DeliveryTermCode implements Serializable {

  @Column(name = "code", nullable = false, unique = true)
  private final String value;

  public DeliveryTermCode(String value) {
    if (StringUtils.trimToNull(value) == null) {
      throw new LocalizedValidationException("validation.delivery_term.code.empty");
    }

    if (value.length() > 50) {
      throw new LocalizedValidationException("validation.delivery_term.code.max_length");
    }

    this.value = value;
  }

  public DeliveryTermCode() {
    this("CODE");
  }

  @Override
  public String toString() {
    return value;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (!(o instanceof DeliveryTermCode deliveryTermCode)) {
      return false;
    }
    return Objects.equals(value, deliveryTermCode.value);
  }

  @Override
  public int hashCode() {
    return getClass().hashCode();
  }
}
