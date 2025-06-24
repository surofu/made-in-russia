package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.command.product.CreateProductCommand;
import com.surofu.madeinrussia.application.command.productReview.CreateProductReviewCommand;
import com.surofu.madeinrussia.application.command.productReview.UpdateProductReviewCommand;
import com.surofu.madeinrussia.application.dto.*;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.dto.error.ValidationExceptionDto;
import com.surofu.madeinrussia.application.dto.page.GetProductReviewPageDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.product.ProductDescription;
import com.surofu.madeinrussia.core.model.product.ProductTitle;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReviewContent;
import com.surofu.madeinrussia.core.model.product.productReview.ProductReviewRating;
import com.surofu.madeinrussia.core.service.product.ProductService;
import com.surofu.madeinrussia.core.service.product.operation.*;
import com.surofu.madeinrussia.core.service.productReview.ProductReviewService;
import com.surofu.madeinrussia.core.service.productReview.operation.CreateProductReview;
import com.surofu.madeinrussia.core.service.productReview.operation.DeleteProductReview;
import com.surofu.madeinrussia.core.service.productReview.operation.GetProductReviewPageByProductId;
import com.surofu.madeinrussia.core.service.productReview.operation.UpdateProductReview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
@Tag(name = "Products", description = "API for managing product")
public class ProductRestController {

    private final ProductService productService;
    private final ProductReviewService productReviewService;

    private final GetProductById.Result.Processor<ResponseEntity<?>> getProductByIdProcessor;
    private final GetProductCategoryByProductId.Result.Processor<ResponseEntity<?>> getProductCategoryByProductIdProcessor;
    private final GetProductDeliveryMethodsByProductId.Result.Processor<ResponseEntity<?>> getProductDeliveryMethodsByProductIdProcessor;
    private final GetProductMediaByProductId.Result.Processor<ResponseEntity<?>> getProductMediaByProductIdProcessor;
    private final GetProductCharacteristicsByProductId.Result.Processor<ResponseEntity<?>> getProductCharacteristicsByProductIdProcessor;
    private final GetProductReviewPageByProductId.Result.Processor<ResponseEntity<?>> getProductReviewPageByProductIdProcessor;
    private final GetProductFaqByProductId.Result.Processor<ResponseEntity<?>> getProductFaqByProductIdProcessor;
    private final CreateProductReview.Result.Processor<ResponseEntity<?>> createProductReviewProcessor;
    private final UpdateProductReview.Result.Processor<ResponseEntity<?>> updateProductReviewProcessor;
    private final DeleteProductReview.Result.Processor<ResponseEntity<?>> deleteProductReviewProcessor;
    private final CreateProduct.Result.Processor<ResponseEntity<?>> createProductProcessor;

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
            @RequestParam(required = false)
            Integer minRating,

            @Parameter(
                    name = "maxRating",
                    description = "Maximum product review rating filter (inclusive)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "number", example = "10000")
            )
            @RequestParam(required = false)
            Integer maxRating
    ) {
        GetProductReviewPageByProductId operation = GetProductReviewPageByProductId.of(productId, page, size, minRating, maxRating);
        return productReviewService.getProductReviewPageByProductId(operation).process(getProductReviewPageByProductIdProcessor);
    }

    @PostMapping("{productId}/reviews")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Create product review",
            description = "Create product review",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully created product review",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ValidationExceptionDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product or review not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> createProductReview(
            @Parameter(
                    name = "productId",
                    description = "ID of the product",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long productId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody @Valid CreateProductReviewCommand createProductReviewCommand
    ) {
        CreateProductReview operation = CreateProductReview.of(
                productId,
                securityUser,
                ProductReviewContent.of(createProductReviewCommand.text()),
                ProductReviewRating.of(createProductReviewCommand.rating())
        );
        return productReviewService.createProductReview(operation).process(createProductReviewProcessor);
    }

    @PatchMapping("{productId}/reviews/{productReviewId}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Update product review by id",
            description = "Update product review by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully updated product review",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ValidationExceptionDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product or review not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> createProductReviewById(
            @Parameter(
                    name = "productId",
                    description = "ID of the product",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long productId,

            @Parameter(
                    name = "productReviewId",
                    description = "ID of the product review to be updated",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long productReviewId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody @Valid UpdateProductReviewCommand updateProductReviewCommand
    ) {
        UpdateProductReview operation = UpdateProductReview.of(
                productId,
                productReviewId,
                securityUser,
                ProductReviewContent.of(updateProductReviewCommand.text()),
                ProductReviewRating.of(updateProductReviewCommand.rating())
        );
        return productReviewService.updateProductReview(operation).process(updateProductReviewProcessor);
    }

    @DeleteMapping("{productId}/reviews/{productReviewId}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Delete product review by id",
            description = "Delete product review by id",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted product review",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = ValidationExceptionDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product or review not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> deleteProductReviewById(
            @Parameter(
                    name = "productId",
                    description = "ID of the product",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long productId,

            @Parameter(
                    name = "productReviewId",
                    description = "ID of the product review to be updated",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long productReviewId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        DeleteProductReview operation = DeleteProductReview.of(
                productId,
                productReviewId,
                securityUser
        );
        return productReviewService.deleteProductReview(operation).process(deleteProductReviewProcessor);
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

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_VENDOR')")
    @Operation(
            summary = "Create new product",
            description = "Creates a new product with media files, prices, characteristics and FAQ",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid product data or validation error",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - authentication required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - ROLE_VENDOR required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> createProduct(
            @Parameter(
                    description = "Product data in JSON format",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = CreateProductCommand.class)
                    )
            )
            @RequestPart("data") @Valid CreateProductCommand createProductCommand,

            @Parameter(
                    description = "Media files for the product (images, videos)",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestPart("productMedia") List<MultipartFile> productMedia,

            @Parameter(
                    description = "Media files for the product (images, videos)",
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestPart(value = "aboutVendorMedia", required = false) List<MultipartFile> productVendorDetailsMedia,

            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        if (createProductCommand.prices() == null || createProductCommand.prices().isEmpty()) {
            String message = "Цены товара не могут быть пустыми";
            SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        }

        if (createProductCommand.characteristics() == null || createProductCommand.characteristics().isEmpty()) {
            String message = "Характеристики товара не могут быть пустыми";
            SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        }

        if (productMedia == null || productMedia.isEmpty()) {
            String message = "Медиа файлы товара не могут быть пустыми";
            SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        }

        CreateProduct operation = CreateProduct.of(
                securityUser,
                ProductTitle.of(createProductCommand.title()),
                ProductDescription.of(
                        createProductCommand.mainDescription(),
                        createProductCommand.furtherDescription()
                ),
                createProductCommand.categoryId(),
                createProductCommand.deliveryMethodIds(),
                createProductCommand.prices(),
                createProductCommand.characteristics(),
                createProductCommand.faq() == null ? new ArrayList<>() : createProductCommand.faq(),
                createProductCommand.deliveryMethodDetails() == null ? new ArrayList<>() : createProductCommand.deliveryMethodDetails(),
                createProductCommand.packageOptions() == null ? new ArrayList<>() : createProductCommand.packageOptions(),
                createProductCommand.aboutVendor(),
                createProductCommand.aboutVendor().mediaAltTexts() == null ? new ArrayList<>() : createProductCommand.aboutVendor().mediaAltTexts(),
                createProductCommand.minimumOrderQuantity(),
                createProductCommand.discountExpirationDate(),
                productMedia,
                productVendorDetailsMedia == null ? new ArrayList<>() : productVendorDetailsMedia
        );
        return productService.createProduct(operation).process(createProductProcessor);
    }
}
