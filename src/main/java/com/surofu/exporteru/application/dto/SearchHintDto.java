package com.surofu.exporteru.application.dto;

import com.surofu.exporteru.application.dto.category.CategoryHintDto;
import com.surofu.exporteru.application.dto.product.ProductHintDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "SearchHint")
public final class SearchHintDto implements Serializable {
    private CategoryHintDto category;
    private List<ProductHintDto> products;
}
