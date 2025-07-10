package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.CategoryDto;
import com.surofu.madeinrussia.core.model.category.CategorySlug;
import com.surofu.madeinrussia.core.service.category.CategoryService;
import com.surofu.madeinrussia.core.service.category.operation.GetCategories;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryById;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryBySlug;
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
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/categories")
@Tag(name = "Categories", description = "API for managing product categories")
public class CategoryRestController {
    private final CategoryService service;

    private final GetCategories.Result.Processor<ResponseEntity<?>> getCategoriesProcessor;
    private final GetCategoryById.Result.Processor<ResponseEntity<?>> getCategoryByIdProcessor;
    private final GetCategoryBySlug.Result.Processor<ResponseEntity<?>> getCategoryBySlugProcessor;

    @GetMapping
    @Operation(
            summary = "Get all categories",
            description = "Retrieves a list of all available product categories",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved categories",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = CategoryDto.class))
                            )
                    )
            }
    )
    public ResponseEntity<?> getCategories() {
        Locale locale = LocaleContextHolder.getLocale();
        GetCategories operation = GetCategories.of(locale);
        return service.getCategories(operation).process(getCategoriesProcessor);
    }

    @GetMapping("/{categoryIdOrSlug}")
    @Operation(
            summary = "Get category by ID or slug",
            description = "Retrieves a single category by its unique identifier or unique slug",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Category found and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryDto.class))
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Category not found",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<?> getCategoryByIdOrSlug(
            @Parameter(
                    name = "categoryIdOrSlug",
                    description = "ID or slug of the category to retrieve",
                    required = true,
                    examples = {
                            @ExampleObject(
                                    name = "categoryId",
                                    description = "ID of the category to retrieve",
                                    value = "1"
                            ),
                            @ExampleObject(
                                    name = "categorySlug",
                                    description = "Slug of the category to retrieve",
                                    value = "l1_rastenievodstvo-i-zhivotnovodstvo"
                            )
                    }
            )
            @PathVariable
            String categoryIdOrSlug
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        try {
            long categoryId = Long.parseLong(categoryIdOrSlug);
            GetCategoryById operation = GetCategoryById.of(categoryId, locale);
            return service.getCategoryById(operation).process(getCategoryByIdProcessor);
        } catch (NumberFormatException e) {
            GetCategoryBySlug operation = GetCategoryBySlug.of(CategorySlug.of(categoryIdOrSlug), locale);
            return service.getCategoryBySlug(operation).process(getCategoryBySlugProcessor);
        }
    }
}
