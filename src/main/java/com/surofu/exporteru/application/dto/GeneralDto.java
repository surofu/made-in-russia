package com.surofu.exporteru.application.dto;

import com.surofu.exporteru.application.dto.advertisement.AdvertisementDto;
import com.surofu.exporteru.application.dto.category.CategoryDto;
import com.surofu.exporteru.application.dto.product.ProductSummaryViewDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

import java.io.Serializable;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(name = "General")
public class GeneralDto implements Serializable {
    private Page<ProductSummaryViewDto> products;
    private List<CategoryDto> categories;
    private List<CategoryDto> allCategories;
    private List<AdvertisementDto> advertisements;
}
