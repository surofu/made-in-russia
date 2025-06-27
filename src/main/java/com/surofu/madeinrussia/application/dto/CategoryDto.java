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
                  "imageUrl": "https://images.unsplash.com/photo-1515446134809-993c501ca304?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
                  "children": [
                        {
                          "id": 1,
                          "name": "Wood",
                          "slug": "l2_wood",
                          "imageUrl": null,
                          "children": [],
                          "creationDate": "2025-05-04T09:17:20.767615Z",
                          "lastModificationDate": "2025-05-04T09:17:20.767615Z"
                        },
                        {
                          "id": 2,
                          "name": "Stone",
                          "slug": "l2_stone",
                          "imageUrl": null,
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
            description = "Image url of the category",
            example = "https://images.unsplash.com/photo-1515446134809-993c501ca304?q=80&w=2070&auto=format&fit=crop&ixlib=rb-4.1.0&ixid=M3wxMjA3fDB8MHxwaG90by1wYWdlfHx8fGVufDB8fHx8fA%3D%3D",
            maxLength = 20_000
    )
    private String imageUrl;

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
                .imageUrl(category.getImageUrl() == null ? null : category.getImageUrl().getValue())
                .childrenCount(category.getChildrenCount())
                .children(category.getChildren().stream().map(CategoryDto::of).toList())
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
                .imageUrl(category.getImageUrl() == null ? null : category.getImageUrl().getValue())
                .childrenCount(category.getChildrenCount())
                .children(new ArrayList<>())
                .creationDate(category.getCreationDate().getValue())
                .lastModificationDate(category.getLastModificationDate().getValue())
                .build();
    }
}