package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.category.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "Category",
        description = "Represents a product category with metadata",
        example = """
                {
                  "id": 5,
                  "name": "Smartphones & Accessories",
                  "creationDate": "2025-05-04T09:17:20.767615Z",
                  "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                }
                """
)
public final class CategoryDto implements Serializable {

    @Schema(
            description = "Unique identifier of the category",
            example = "5",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long id;

    @Schema(
            description = "Category unique nam for searching and links",
            example = "l1_rastenievodstvo-i-zhivotnovodstvo",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String slug;

    @Schema(
            description = "Display name of the category",
            example = "Home Appliances",
            maxLength = 255,
            requiredMode = Schema.RequiredMode.REQUIRED
    )
    private String name;

    private List<CategoryDto> children;

    @Schema(
            description = "Timestamp when the category was created in the system",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime creationDate;

    @Schema(
            description = "Timestamp when the category was last updated",
            example = "2025-05-04T09:17:20.767615Z",
            type = "string",
            format = "date-time",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private ZonedDateTime lastModificationDate;

    @Schema(hidden = true)
    public static CategoryDto of(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .slug(category.getSlug().getValue())
                .name(category.getName().getValue())
                .children(category.getChildren().stream().map(CategoryDto::ofWithoutChildren).toList())
                .creationDate(category.getCreationDate().getValue())
                .lastModificationDate(category.getLastModificationDate().getValue())
                .build();
    }

    @Schema(hidden = true)
    public static CategoryDto ofWithoutChildren(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .slug(category.getSlug().getValue())
                .name(category.getName().getValue())
                .children(List.of())
                .creationDate(category.getCreationDate().getValue())
                .lastModificationDate(category.getLastModificationDate().getValue())
                .build();
    }
}