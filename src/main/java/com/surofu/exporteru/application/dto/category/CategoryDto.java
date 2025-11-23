package com.surofu.exporteru.application.dto.category;

import com.surofu.exporteru.core.model.category.Category;
import com.surofu.exporteru.infrastructure.persistence.category.CategoryView;
import io.swagger.v3.oas.annotations.media.Schema;
import java.io.Serializable;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(
    name = "Category",
    description = "Represents a product category with metadata"
)
public final class CategoryDto implements Serializable {
  private Long id;
  private String slug;
  private String name;
  private String title;
  private String label;
  private String description;
  private String imageUrl;
  private String iconUrl;
  @Builder.Default
  private List<String> okved = new ArrayList<>();
  @Builder.Default
  private List<CategoryDto> children = new ArrayList<>();
  private Long childrenCount;
  private ZonedDateTime creationDate;
  private ZonedDateTime lastModificationDate;

  @Schema(hidden = true)
  public static CategoryDto of(Category category, Locale locale) {
    return CategoryDto.builder()
        .id(category.getId())
        .slug(category.getSlug().getValue())
        .name(category.getName() != null ? category.getName().getLocalizedValue(locale) : "")
        .label(category.getLabel() != null ? category.getLabel().getLocalizedValue(locale) : "")
        .title(category.getTitle() != null ? category.getTitle().getLocalizedValue(locale) : "")
        .description(category.getDescription() != null ?
            category.getDescription().getLocalizedValue(locale) : "")
        .imageUrl(category.getImageUrl() == null ? null : category.getImageUrl().getValue())
        .iconUrl(category.getIconUrl() == null ? null : category.getIconUrl().getValue())
        .childrenCount(category.getChildrenCount())
        .children(category.getChildren().stream()
            .map(c -> CategoryDto.of(c, locale))
            .toList())
        .creationDate(category.getCreationDate().getValue())
        .lastModificationDate(category.getLastModificationDate().getValue())
        .build();
  }

  @Schema(hidden = true)
  public static CategoryDto ofWithoutChildren(Category category, Locale locale) {
    return CategoryDto.builder()
        .id(category.getId())
        .slug(category.getSlug().getValue())
        .name(category.getName() != null ? category.getName().getLocalizedValue(locale) : "")
        .title(category.getTitle() != null ? category.getTitle().getLocalizedValue(locale) : "")
        .label(category.getLabel() != null ? category.getLabel().getLocalizedValue(locale) : "")
        .description(category.getDescription() != null ?
            category.getDescription().getLocalizedValue(locale) : "")
        .imageUrl(category.getImageUrl() == null ? null : category.getImageUrl().getValue())
        .iconUrl(category.getIconUrl() == null ? null : category.getIconUrl().getValue())
        .childrenCount(category.getChildrenCount())
        .children(new ArrayList<>())
        .creationDate(category.getCreationDate().getValue())
        .lastModificationDate(category.getLastModificationDate().getValue())
        .build();
  }

  @Schema(hidden = true)
  public static CategoryDto ofWithoutChildren(CategoryView view) {
    return CategoryDto.builder()
        .id(view.getId())
        .slug(view.getSlug())
        .name(view.getName())
        .title(view.getTitle())
        .label(view.getLabel())
        .description(view.getDescription())
        .imageUrl(view.getImageUrl())
        .iconUrl(view.getIconUrl())
        .childrenCount(view.getChildrenCount())
        .children(new ArrayList<>())
        .creationDate(view.getCreationDate().atZone(ZoneId.systemDefault()))
        .lastModificationDate(view.getLastModificationDate().atZone(ZoneId.systemDefault()))
        .build();
  }

  public CategoryDto copy() {
    return copy(4);
  }

  private CategoryDto copy(int maxDepth) {
    if (maxDepth <= 0) {
      return CategoryDto.builder()
          .id(this.getId())
          .slug(this.getSlug())
          .name(this.getName())
          .title(this.getTitle())
          .label(this.getLabel())
          .description(this.getDescription())
          .imageUrl(this.getImageUrl())
          .iconUrl(this.getIconUrl())
          .okved(this.getOkved())
          .childrenCount(this.getChildrenCount())
          .children(new ArrayList<>())
          .creationDate(this.getCreationDate())
          .lastModificationDate(this.getLastModificationDate())
          .build();
    }

    return CategoryDto.builder()
        .id(this.getId())
        .slug(this.getSlug())
        .name(this.getName())
        .label(this.getLabel())
        .title(this.getTitle())
        .description(this.getDescription())
        .imageUrl(this.getImageUrl())
        .iconUrl(this.getIconUrl())
        .okved(this.getOkved() == null ? null : new ArrayList<>(this.getOkved()))
        .childrenCount(this.getChildrenCount())
        .children(this.getChildren().stream()
            .map(child -> child.copy(maxDepth - 1))
            .toList())
        .creationDate(this.getCreationDate())
        .lastModificationDate(this.getLastModificationDate())
        .build();
  }
}