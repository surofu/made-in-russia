package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.GetProductsDto;
import com.surofu.madeinrussia.application.query.product.GetProductsQuery;
import com.surofu.madeinrussia.core.service.product.ProductService;
import com.surofu.madeinrussia.core.service.product.operation.GetProducts;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
@Tag(name = "Products", description = "API for managing product")
public class ProductsRestController {

    private final ProductService productService;

    private final GetProducts.Result.Processor<ResponseEntity<?>> getProductsProcessor;

    @GetMapping
    @Operation(
            summary = "Get filtered and paginated list of products",
            description = "Retrieves a page of products with optional filtering by category and price range",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved page of products",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = GetProductsDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<?> getProducts(
            @Parameter(
                    name = "page",
                    description = "Zero-based page index (0..N)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
            )
            @RequestParam(defaultValue = "0") int page,

            @Parameter(
                    name = "size",
                    description = "Number of products per page",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")
            )
            @RequestParam(defaultValue = "10") int size,

            @Parameter(
                    name = "categoryId",
                    description = "Filter products by category ID",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "integer", format = "int64", example = "1")
            )
            @RequestParam(required = false) Long categoryId,

            @Parameter(
                    name = "minPrice",
                    description = "Minimum price filter (inclusive)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "number", format = "decimal", example = "10.50")
            )
            @RequestParam(required = false) BigDecimal minPrice,

            @Parameter(
                    name = "maxPrice",
                    description = "Maximum price filter (inclusive)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "number", format = "decimal", example = "99.99")
            )
            @RequestParam(required = false) BigDecimal maxPrice
    ) {
        GetProductsQuery query = new GetProductsQuery(
                page, size, categoryId, minPrice, maxPrice);

        return productService.getProducts(GetProducts.of(query)).process(getProductsProcessor);
    }
}
