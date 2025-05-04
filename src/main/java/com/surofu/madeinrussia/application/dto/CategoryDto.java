package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.core.model.category.Category;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CategoryDto {
    private Long id;
    private String name;
    private LocalDateTime creationDate;
    private LocalDateTime lastModificationDate;

    public static CategoryDto of(Category category) {
        return CategoryDto.builder()
                .id(category.getId())
                .name(category.getName().getName())
                .creationDate(category.getCreationDate())
                .lastModificationDate(category.getLastModificationDate())
                .build();
    }
}
