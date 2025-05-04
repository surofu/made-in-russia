package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.GetProductsDto;
import com.surofu.madeinrussia.application.dto.ProductDto;
import com.surofu.madeinrussia.application.query.GetProductByIdQuery;
import com.surofu.madeinrussia.application.query.GetProductsQuery;
import com.surofu.madeinrussia.core.model.product.Product;
import com.surofu.madeinrussia.core.repository.specification.ProductSpecifications;
import com.surofu.madeinrussia.core.service.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@AllArgsConstructor
@RequestMapping("api/v1/products")
@Tag(name = "Products")
public class ProductRestController {

    private final ProductService service;

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
        Specification<Product> specification = Specification
                .where(ProductSpecifications.hasCategory(categoryId))
                .and(ProductSpecifications.priceBetween(minPrice, maxPrice));

        GetProductsQuery query = GetProductsQuery.of(specification, PageRequest.of(page, size));
        return ResponseEntity.ok(service.getProducts(query));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get product by ID",
            description = "Retrieves a single product by its unique identifier",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product found and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(schema = @Schema(hidden = true))
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid ID supplied",
                            content = @Content(schema = @Schema(hidden = true))
                    )
            }
    )
    public ResponseEntity<?> getProductById(
            @Parameter(
                    name = "id",
                    description = "ID of the product to be retrieved",
                    required = true,
                    example = "123",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long id
    ) {
        var product = service.getProductById(GetProductByIdQuery.of(id));

        if (product.isPresent()) {
            return new ResponseEntity<>(product.get(), HttpStatus.OK);
        }

        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}
