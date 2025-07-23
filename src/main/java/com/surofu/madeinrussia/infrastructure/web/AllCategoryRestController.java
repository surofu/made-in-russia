package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.category.CategoryDto;
import com.surofu.madeinrussia.core.service.category.CategoryService;
import com.surofu.madeinrussia.core.service.category.operation.GetAllCategories;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Locale;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/all-categories")
@Tag(name = "Categories", description = "API for managing product categories")
public class AllCategoryRestController {
    private final CategoryService service;

    private final GetAllCategories.Result.Processor<ResponseEntity<?>> getAllCategoriesProcessor;

    @GetMapping
    @Operation(
            summary = "Get all categories with all children",
            description = "Retrieves a list of all available product categories with their children",
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
    public ResponseEntity<?> getAllCategories() {
        Locale locale = LocaleContextHolder.getLocale();
        GetAllCategories operation = GetAllCategories.of(locale);
        return service.getAllCategories(operation).process(getAllCategoriesProcessor);
    }
}
