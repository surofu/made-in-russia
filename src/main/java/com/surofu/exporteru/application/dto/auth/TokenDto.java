package com.surofu.exporteru.application.dto.auth;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "SingleToken",
        description = "Represents a single token DTO"
)
public final class TokenDto implements Serializable {
    private String accessToken;

    public static TokenDto of(String accessToken) {
        return TokenDto.builder()
                .accessToken(accessToken)
                .build();
    }
}
