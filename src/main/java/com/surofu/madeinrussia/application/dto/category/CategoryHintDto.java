package com.surofu.madeinrussia.application.dto.category;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class CategoryHintDto implements Serializable {
    Long id;
    String name;
    String image;
}
