package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.application.dto.product.ProductHintDto;
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
public final class SearchHintDto implements Serializable {
    CategoryHintDto category;
    List<ProductHintDto> products;
}
