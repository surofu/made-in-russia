package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.category.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "Category",
        description = "Represents a product category DTO"
)
public final class CategoryDto implements Serializable {

    @Schema(
            description = "Unique identifier of the category",
            example = "1"
    )
    private Long id;

    @Schema(
            description = "Name of the category",
            example = "Electronics",
            maxLength = 100
    )
    private String name;

    @Schema(
            description = "Timestamp when the category was created",
            example = "2023-07-15T14:30:00",
            type = "string",
            format = "date-time"
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when the category was last modified",
            example = "2023-07-20T09:15:30",
            type = "string",
            format = "date-time"
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static CategoryDto of(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName().getName())
                .creationDate(category.getCreationDate().getCreationDate())
                .lastModificationDate(category.getLastModificationDate().getLastModificationDate())
                .build();
    }
}
