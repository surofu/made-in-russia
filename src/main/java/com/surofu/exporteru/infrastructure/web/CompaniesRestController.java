package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.core.model.category.CategorySlug;
import com.surofu.exporteru.core.model.okved.OkvedCompany;
import com.surofu.exporteru.core.service.company.CompanyService;
import com.surofu.exporteru.core.service.company.operation.GetCompaniesByCategorySlug;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/companies")
@Tag(
        name = "Companies",
        description = "API for accessing company information by categories"
)
public class CompaniesRestController {

    private final CompanyService service;

    private final GetCompaniesByCategorySlug.Result.Processor<ResponseEntity<?>> getCompaniesByCategorySlugProcessor;

    @GetMapping("{categorySlug}")
    @PreAuthorize("permitAll()")
    @Operation(
            summary = "Get companies by category",
            description = "Retrieves list of companies belonging to the specified category",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved companies list",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(
                                            schema = @Schema(
                                                    implementation = OkvedCompany.class,
                                                    example = """
                                                            {
                                                              "name": "ООО Ромашка",
                                                              "inn": "7701234567",
                                                              "ageInYears": 5
                                                            }
                                                            """
                                            )
                                    ),
                                    examples = {
                                            @ExampleObject(
                                                    name = "Single company",
                                                    value = """
                                                            [
                                                              {
                                                                "name": "ООО Ромашка",
                                                                "inn": "7701234567",
                                                                "ageInYears": 5
                                                              }
                                                            ]
                                                            """
                                            ),
                                            @ExampleObject(
                                                    name = "Multiple companies",
                                                    value = """
                                                            [
                                                              {
                                                                "name": "ООО Ромашка",
                                                                "inn": "7701234567",
                                                                "ageInYears": 5
                                                              },
                                                              {
                                                                "name": "АО Вектор",
                                                                "inn": "7707654321",
                                                                "ageInYears": 12
                                                              }
                                                            ]
                                                            """
                                            )
                                    }
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Category not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class,
                                            example = """
                                                    {
                                                      "status": "error",
                                                      "message": "Category not found"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid category slug format",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class,
                                            example = """
                                                    {
                                                      "status": "error",
                                                      "message": "Invalid category slug format"
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> findByCategoryId(
            @Parameter(
                    name = "categorySlug",
                    description = "Identifier of the category",
                    required = true,
                    examples = {
                            @ExampleObject(
                                    name = "Уголь и антрацит",
                                    value = "l2_ugol-i-antracit"
                            ),
                            @ExampleObject(
                                    name = "Обогащенный антрацит",
                                    value = "l3_obogashchennyj-antracit"
                            )
                    }
            )
            @PathVariable String categorySlug) {
        Locale locale = LocaleContextHolder.getLocale();
        GetCompaniesByCategorySlug operation = GetCompaniesByCategorySlug.of(CategorySlug.of(categorySlug), locale);
        return service.getByCategorySlug(operation).process(getCompaniesByCategorySlugProcessor);
    }
}
