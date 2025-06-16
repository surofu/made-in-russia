package com.surofu.madeinrussia.application.dto.error;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Schema(
        name = "ProductNotFoundByIdResponseError",
        description = "Represents a product not found by id response DTO"
)
public class UserNotFoundByIdResponseErrorDto extends SimpleResponseErrorDto {
    private UserNotFoundByIdResponseErrorDto(Long userId) {
        super(HttpStatus.NOT_FOUND.getReasonPhrase(), String.format("Пользователь с ID '%s' не найден", userId), HttpStatus.NOT_FOUND.value());
    }

    @Schema(hidden = true)
    public static UserNotFoundByIdResponseErrorDto of(Long userId) {
        return new UserNotFoundByIdResponseErrorDto(userId);
    }
}
