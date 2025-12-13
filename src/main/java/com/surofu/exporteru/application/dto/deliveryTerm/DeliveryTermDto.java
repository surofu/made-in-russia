package com.surofu.exporteru.application.dto.deliveryTerm;

import com.surofu.exporteru.core.model.deliveryTerm.DeliveryTerm;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class DeliveryTermDto implements Serializable {
  private Long id;
  private String code;
  private String name;
  private String description;

  public static DeliveryTermDto of(DeliveryTerm deliveryTerm) {
    return DeliveryTermDto.builder()
        .id(deliveryTerm.getId())
        .code(deliveryTerm.getCode().getValue())
        .name(deliveryTerm.getName().getLocalizedValue())
        .description(deliveryTerm.getDescription().getLocalizedValue())
        .build();
  }
}
