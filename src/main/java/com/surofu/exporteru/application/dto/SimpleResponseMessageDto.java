package com.surofu.exporteru.application.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "ResponseMessage")
public final class SimpleResponseMessageDto implements Serializable {
  private String message;

  public static SimpleResponseMessageDto of(String message) {
    return SimpleResponseMessageDto.builder()
        .message(message)
        .build();
  }
}
