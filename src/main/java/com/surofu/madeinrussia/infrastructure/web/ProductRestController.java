package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.*;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.dto.page.GetProductReviewPageDto;
import com.surofu.madeinrussia.core.service.product.ProductService;
import com.surofu.madeinrussia.core.service.product.operation.*;
import com.surofu.madeinrussia.core.service.productReview.ProductReviewService;
import com.surofu.madeinrussia.core.service.productReview.operation.GetProductReviewPageByProductId;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
@Tag(name = "Products", description = "API for managing product")
public class ProductRestController {

    private final ProductService productService;
    private final ProductReviewService productReviewService;

    private final GetProductPage.Result.Processor<ResponseEntity<?>> getProductPageProcessor;
    private final GetProductById.Result.Processor<ResponseEntity<?>> getProductByIdProcessor;
    private final GetProductCategoryByProductId.Result.Processor<ResponseEntity<?>> getProductCategoryByProductIdProcessor;
    private final GetProductDeliveryMethodsByProductId.Result.Processor<ResponseEntity<?>> getProductDeliveryMethodsByProductIdProcessor;
    private final GetProductMediaByProductId.Result.Processor<ResponseEntity<?>> getProductMediaByProductIdProcessor;
    private final GetProductCharacteristicsByProductId.Result.Processor<ResponseEntity<?>> getProductCharacteristicsByProductIdProcessor;
    private final GetProductReviewPageByProductId.Result.Processor<ResponseEntity<?>> getProductReviewPageByProductIdProcessor;
    private final GetProductFaqByProductId.Result.Processor<ResponseEntity<?>> getProductFaqByProductIdProcessor;

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
                                    schema = @Schema(implementation = GetProductPageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ValidationExceptionDto.class
                                    )
                            )
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
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @Parameter(
                    name = "size",
                    description = "Number of products per page",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")
            )
            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size,

            @Parameter(
                    name = "title",
                    description = "Title of the product",
                    in = ParameterIn.QUERY
            )
            String title,

            @Parameter(
                    name = "deliveryMethodIds",
                    description = "Filter products by delivery method IDs. Multiple delivery method IDs can be provided",
                    in = ParameterIn.QUERY,
                    schema = @Schema(
                            type = "array",
                            format = "int64",
                            example = "[1, 2]",
                            minLength = 1,
                            maxLength = 40
                    ),
                    explode = Explode.FALSE,
                    examples = {
                            @ExampleObject(
                                    name = "Single delivery method",
                                    value = "1",
                                    description = "Filter by single delivery method ID"
                            ),
                            @ExampleObject(
                                    name = "Multiple delivery methods",
                                    value = "1,2",
                                    description = "Filter by multiple delivery method IDs"
                            )
                    }
            )
            @RequestParam(required = false)
            List<Long> deliveryMethodIds,

            @Parameter(
                    name = "categoryIds",
                    description = "Filter products by category IDs. Multiple category IDs can be provided",
                    in = ParameterIn.QUERY,
                    schema = @Schema(
                            type = "array",
                            format = "int64",
                            example = "[1, 2, 3]",
                            minLength = 1,
                            maxLength = 80
                    ),
                    explode = Explode.FALSE,
                    examples = {
                            @ExampleObject(
                                    name = "Single category",
                                    value = "1",
                                    description = "Filter by single category ID"
                            ),
                            @ExampleObject(
                                    name = "Multiple categories",
                                    value = "1,2,3",
                                    description = "Filter by multiple category IDs"
                            )
                    }
            )
            @RequestParam(required = false)
            List<Long> categoryIds,

            @Parameter(
                    name = "minPrice",
                    description = "Minimum price filter (inclusive)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "number", format = "decimal", example = "1")
            )
            @RequestParam(required = false)
            BigDecimal minPrice,

            @Parameter(
                    name = "maxPrice",
                    description = "Maximum price filter (inclusive)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "number", format = "decimal", example = "100000")
            )
            @RequestParam(required = false)
            BigDecimal maxPrice
    ) {
        GetProductPage operation = GetProductPage.of(page, size, title, deliveryMethodIds, categoryIds, minPrice, maxPrice);
        return productService.getProductPage(operation).process(getProductPageProcessor);
    }

    @GetMapping("{productId}")
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
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> getProductById(
            @Parameter(
                    name = "productId",
                    description = "ID of the product to be retrieved",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable
            Long productId
    ) {
        GetProductById operation = GetProductById.of(productId);
        return productService.getProductById(operation).process(getProductByIdProcessor);
    }

    @GetMapping("{productId}/category")
    @Operation(
            summary = "Get product category by product ID",
            description = "Retrieves a single category by product's unique identifier",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product category found and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = CategoryDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> getProductCategoryByProductId(
            @Parameter(
                    name = "productId",
                    description = "ID of the product to be retrieved",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long productId
    ) {
        GetProductCategoryByProductId operation = GetProductCategoryByProductId.of(productId);
        return productService.getProductCategoryByProductId(operation).process(getProductCategoryByProductIdProcessor);
    }

    @GetMapping("{productId}/delivery-methods")
    @Operation(
            summary = "Get product delivery methods by product ID",
            description = "Retrieves a delivery methods by product's unique identifier",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product delivery methods found and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = DeliveryMethodDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> getProductDeliveryMethodsByProductId(
            @Parameter(
                    name = "productId",
                    description = "ID of the product to be retrieved",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long productId
    ) {
        GetProductDeliveryMethodsByProductId operation = GetProductDeliveryMethodsByProductId.of(productId);
        return productService.getProductDeliveryMethodsByProductId(operation).process(getProductDeliveryMethodsByProductIdProcessor);
    }

    @GetMapping("{productId}/media")
    @Operation(
            summary = "Get product media by product ID",
            description = "Retrieves a array of images and videos by product's unique identifier",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product media found and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ProductMediaDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> getProductMediaByProductId(
            @Parameter(
                    name = "productId",
                    description = "ID of the product to be retrieved",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long productId) {
        GetProductMediaByProductId operation = GetProductMediaByProductId.of(productId);
        return productService.getProductMediaByProductId(operation).process(getProductMediaByProductIdProcessor);
    }

    @GetMapping("{productId}/characteristics")
    @Operation(
            summary = "Get product characteristics by product ID",
            description = "Retrieves a characteristics by product's unique identifier",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product characteristics found and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = ProductCharacteristicDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> getProductCharacteristicsByProductId(
            @Parameter(
                    name = "productId",
                    description = "ID of the product to be retrieved",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long productId
    ) {
        GetProductCharacteristicsByProductId operation = GetProductCharacteristicsByProductId.of(productId);
        return productService.getProductCharacteristicsByProductId(operation).process(getProductCharacteristicsByProductIdProcessor);
    }

    @GetMapping("{productId}/reviews")
    @Operation(
            summary = "Get filtered and paginated list of product reviews",
            description = "Retrieves a page of product reviews with optional filtering by rating range",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved page of product reviews",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = GetProductReviewPageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ValidationExceptionDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> getProductReviewPageByProductId(
            @Parameter(
                    name = "productId",
                    description = "ID of the product to be retrieved",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long productId,

            @Parameter(
                    name = "page",
                    description = "Zero-based page index (0..N)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
            )
            @RequestParam(defaultValue = "0")
            @Min(0)
            int page,

            @Parameter(
                    name = "size",
                    description = "Number of product reviews per page",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")
            )
            @RequestParam(defaultValue = "10")
            @Min(1)
            @Max(100)
            int size,

            @Parameter(
                    name = "minRating",
                    description = "Minimum product review rating filter (inclusive)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "number", example = "1")
            )
            Integer minRating,

            @Parameter(
                    name = "maxRating",
                    description = "Maximum product review rating filter (inclusive)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "number", example = "10000")
            )
            Integer maxRating
    ) {
        GetProductReviewPageByProductId operation = GetProductReviewPageByProductId.of(productId, page, size, minRating, maxRating);
        return productReviewService.getProductReviewPageByProductId(operation).process(getProductReviewPageByProductIdProcessor);
    }

    @GetMapping("{productId}/faq")
    @Operation(
            summary = "Get list of product faq (question and answer)",
            description = "Retrieves a list of product faq by product ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved list of product faq",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = GetProductReviewPageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> getProductFaqByProductId(
            @Parameter(
                    name = "productId",
                    description = "ID of the product to be retrieved",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long productId) {
        GetProductFaqByProductId operation = GetProductFaqByProductId.of(productId);
        return productService.getProductFaqByProductId(operation).process(getProductFaqByProductIdProcessor);
    }
}
