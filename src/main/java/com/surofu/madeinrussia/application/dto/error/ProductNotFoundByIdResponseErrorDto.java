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
public class ProductNotFoundByIdResponseErrorDto extends SimpleResponseErrorDto {
    private ProductNotFoundByIdResponseErrorDto(Long productId) {
        super(HttpStatus.NOT_FOUND.getReasonPhrase(), String.format("Товар с ID '%s' не найден", productId), HttpStatus.NOT_FOUND.value());
    }

    @Schema(hidden = true)
    public static ProductNotFoundByIdResponseErrorDto of(Long productId) {
        return new ProductNotFoundByIdResponseErrorDto(productId);
    }
}
