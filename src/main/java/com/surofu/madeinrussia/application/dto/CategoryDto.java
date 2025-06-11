package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.category.Category;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.ArrayList;
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
                  "slug": "l1_rastenievodstvo-i-zhivotnovodstvo",
                  "children": [
                        {
                          "id": 1,
                          "name": "Wood",
                          "slug": "l2_wood",
                          "children": [],
                          "creationDate": "2025-05-04T09:17:20.767615Z",
                          "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                        },
                        {
                          "id": 2,
                          "name": "Stone",
                          "slug": "l2_stone",
                          "children": [],
                          "creationDate": "2025-05-04T09:17:20.767615Z",
                          "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                        }
                      ],
                  "childrenCount": 2,
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

    @Schema(
            description = "Represents children category",
            type = "array",
            implementation = CategoryDto[].class,
            example = """
                      [
                        {
                          "id": 1,
                          "name": "Wood",
                          "slug": "l2_wood",
                          "children": [],
                          "creationDate": "2025-05-04T09:17:20.767615Z",
                          "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                        },
                        {
                          "id": 2,
                          "name": "Stone",
                          "slug": "l2_stone",
                          "children": [],
                          "creationDate": "2025-05-04T09:17:20.767615Z",
                          "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                        }
                      ]
                    """
    )
    private List<CategoryDto> children = new ArrayList<>();

    @Schema(
            description = "Count of current category children",
            example = "10",
            accessMode = Schema.AccessMode.READ_ONLY
    )
    private Long childrenCount;

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
                .childrenCount(category.getChildrenCount().getValue())
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
                .childrenCount(category.getChildrenCount().getValue())
                .creationDate(category.getCreationDate().getValue())
                .lastModificationDate(category.getLastModificationDate().getValue())
                .build();
    }
}