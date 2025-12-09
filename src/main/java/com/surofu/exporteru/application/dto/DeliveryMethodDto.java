package com.surofu.exporteru.application.dto;

import com.surofu.exporteru.core.model.deliveryMethod.DeliveryMethod;
import com.surofu.exporteru.infrastructure.persistence.deliveryMethod.DeliveryMethodView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "DeliveryMethod")
public final class DeliveryMethodDto implements Serializable {
  private Long id;
  private String name;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  public static DeliveryMethodDto of(DeliveryMethod deliveryMethod) {
    return DeliveryMethodDto.builder()
        .id(deliveryMethod.getId())
        .name(deliveryMethod.getName().getLocalizedValue())
        .creationDate(deliveryMethod.getCreationDate().getValue())
        .lastModificationDate(deliveryMethod.getLastModificationDate().getValue())
        .build();
  }

  public static DeliveryMethodDto of(DeliveryMethodView view) {
    return DeliveryMethodDto.builder()
        .id(view.getId())
        .name(view.getName())
        .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
        .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
        .build();
  }
}
