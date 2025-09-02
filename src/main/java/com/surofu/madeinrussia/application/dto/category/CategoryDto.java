package com.surofu.madeinrussia.application.dto.category;

import com.surofu.madeinrussia.core.model.category.Category;
import com.surofu.madeinrussia.infrastructure.persistence.category.CategoryView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
        name = "Category",
        description = "Represents a product category with metadata"
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
            example = "https://media.tenor.com/x8v1oNUOmg4AAAAM/rickroll-roll.gif",
            maxLength = 20_000
    )
    private String imageUrl;

    @Schema(
            description = "Represents children category",
            type = "array",
            implementation = CategoryDto[].class
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

    @Schema(hidden = true)
    public static CategoryDto ofWithoutChildren(CategoryView view) {
        if (view == null) {
            return null;
        }

        return CategoryDto.builder()
                .id(view.getId())
                .slug(view.getSlug())
                .name(view.getName())
                .imageUrl(view.getImageUrl())
                .childrenCount(view.getChildrenCount())
                .children(new ArrayList<>())
                .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
                .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
                .build();
    }

    public CategoryDto copy() {
        return CategoryDto.builder()
                .id(this.getId())
                .slug(this.getSlug())
                .name(this.getName())
                .imageUrl(this.getImageUrl() == null ? null : this.getImageUrl())
                .childrenCount(this.getChildrenCount())
                .children(this.getChildren().stream().map(CategoryDto::copy).toList())
                .creationDate(this.getCreationDate())
                .lastModificationDate(this.getLastModificationDate())
                .build();
    }
}