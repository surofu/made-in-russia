package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.CategoryDto;
import com.surofu.madeinrussia.application.query.category.GetCategoryByIdQuery;
import com.surofu.madeinrussia.core.service.category.CategoryService;
import com.surofu.madeinrussia.core.service.category.operation.GetCategories;
import com.surofu.madeinrussia.core.service.category.operation.GetCategoryById;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Positive;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/categories")
@Tag(name = "Categories", description = "API for managing product categories")
public class CategoryRestController {
    private final CategoryService service;

    private final GetCategories.Result.Processor<ResponseEntity<?>> getCategoriesProcessor;
    private final GetCategoryById.Result.Processor<ResponseEntity<?>> getCategoryByIdProcessor;

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
        return service.getCategories().process(getCategoriesProcessor);
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get category by ID",
            description = "Retrieves a single category by its unique identifier",
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
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<?> getCategoryById(
            @Parameter(
                    name = "id",
                    description = "ID of the category to retrieve",
                    required = true,
                    example = "1",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable
            Long id
    ) {
        GetCategoryByIdQuery query = new GetCategoryByIdQuery(id);
        GetCategoryById operation = GetCategoryById.of(query);
        return service.getCategoryById(operation).process(getCategoryByIdProcessor);
    }
}
