package com.surofu.exporteru.application.command.category;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Schema(
    name = "SaveCategoryCommand",
    description = "DTO for creating or updating a category",
    requiredProperties = {"name", "nameTranslations", "slug"}
)
public record SaveCategoryCommand(
    String name,
    Map<String, String> nameTranslations,
    String title,
    Map<String, String> titleTranslations,
    String label,
    Map<String, String> labelTranslations,
    String description,
    Map<String, String> descriptionTranslations,
    String slug,
    Long parentId,
    List<String> okvedCategories
) {
}
