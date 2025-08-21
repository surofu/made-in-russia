package com.surofu.madeinrussia.application.dto;

import com.surofu.madeinrussia.application.dto.category.CategoryDto;
import com.surofu.madeinrussia.application.dto.product.ProductSummaryViewDto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeneralDto implements Serializable {
    Page<ProductSummaryViewDto> products;
    List<CategoryDto> categories;
    List<CategoryDto> allCategories;
}
