package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.command.product.create.CreateProductCommand;
import com.surofu.madeinrussia.application.command.product.review.CreateProductReviewCommand;
import com.surofu.madeinrussia.application.command.product.review.UpdateProductReviewCommand;
import com.surofu.madeinrussia.application.command.product.update.UpdateProductCommand;
import com.surofu.madeinrussia.application.dto.DeliveryMethodDto;
import com.surofu.madeinrussia.application.dto.SearchHintDto;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.category.CategoryDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.dto.error.ValidationExceptionDto;
import com.surofu.madeinrussia.application.dto.product.GetProductReviewPageDto;
import com.surofu.madeinrussia.application.dto.product.ProductCharacteristicDto;
import com.surofu.madeinrussia.application.dto.product.ProductDto;
import com.surofu.madeinrussia.application.dto.product.ProductMediaDto;
import com.surofu.madeinrussia.application.dto.translation.HstoreTranslationDto;
import com.surofu.madeinrussia.application.dto.translation.TranslationDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.model.product.ProductArticleCode;
import com.surofu.madeinrussia.core.model.product.ProductDescription;
import com.surofu.madeinrussia.core.model.product.ProductTitle;
import com.surofu.madeinrussia.core.model.product.review.ProductReviewContent;
import com.surofu.madeinrussia.core.model.product.review.ProductReviewRating;
import com.surofu.madeinrussia.core.service.product.ProductService;
import com.surofu.madeinrussia.core.service.product.operation.*;
import com.surofu.madeinrussia.core.service.product.review.ProductReviewService;
import com.surofu.madeinrussia.core.service.product.review.operation.CreateProductReview;
import com.surofu.madeinrussia.core.service.product.review.operation.DeleteProductReview;
import com.surofu.madeinrussia.core.service.product.review.operation.GetProductReviewPageByProductId;
import com.surofu.madeinrussia.core.service.product.review.operation.UpdateProductReview;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.StringToClassMapItem;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.ZonedDateTime;
import java.util.*;

@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products")
@Tag(name = "Products", description = "API for managing product")
public class ProductRestController {

    private final ProductService productService;
    private final ProductReviewService productReviewService;
    private final LocalizationManager localizationManager;

    private final GetProductById.Result.Processor<ResponseEntity<?>> getProductByIdProcessor;
    private final GetProductWithTranslationsById.Result.Processor<ResponseEntity<?>> getProductWithTranslationsByIdProcessor;
    private final GetProductByArticle.Result.Processor<ResponseEntity<?>> getProductByArticleProcessor;
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
    private final UpdateProduct.Result.Processor<ResponseEntity<?>> updateProductProcessor;
    private final GetSearchHints.Result.Processor<ResponseEntity<?>> getSearchHintsProcessor;
    private final DeleteProductById.Result.Processor<ResponseEntity<?>> deleteProductByIdProcessor;

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
            Long productId,

            @RequestParam(name = "hasTranslations", required = false, defaultValue = "false")
            Boolean hasTranslations
    ) {
        Locale locale = LocaleContextHolder.getLocale();

        if (hasTranslations) {
            GetProductWithTranslationsById operation = GetProductWithTranslationsById.of(productId, locale);
            return productService.getProductWithTranslationsByProductId(operation).process(getProductWithTranslationsByIdProcessor);
        }

        GetProductById operation = GetProductById.of(locale, productId);
        return productService.getProductById(operation).process(getProductByIdProcessor);
    }

    @GetMapping("article/{article}")
    @Operation(
            summary = "Get product by article code",
            description = "Retrieves a single product by its unique article code/SKU identifier",
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
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid article code format",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ValidationExceptionDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> getProductByArticle(
            @Parameter(
                    name = "article",
                    description = "Article code/SKU of the product to be retrieved",
                    required = true,
                    example = "ABCD-1234",
                    schema = @Schema(
                            type = "string",
                            pattern = "^[A-Za-z]{4}-[0-9]{4}$",
                            minLength = 1,
                            maxLength = 50
                    )
            )
            @PathVariable String article
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        GetProductByArticle operation = GetProductByArticle.of(locale, ProductArticleCode.of(article));
        return productService.getProductByArticle(operation).process(getProductByArticleProcessor);
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
        Locale locale = LocaleContextHolder.getLocale();
        GetProductReviewPageByProductId operation = GetProductReviewPageByProductId.of(productId, page, size, minRating, maxRating, locale);
        return productReviewService.getProductReviewPageByProductId(operation).process(getProductReviewPageByProductIdProcessor);
    }

    @PostMapping(value = "{productId}/reviews", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
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
            @RequestPart("data") CreateProductReviewCommand command,
            @RequestPart(value = "media", required = false) List<MultipartFile> media
    ) {
        CreateProductReview operation = CreateProductReview.of(
                productId,
                securityUser,
                ProductReviewContent.of(command.text()),
                ProductReviewRating.of(command.rating()),
                Objects.requireNonNullElse(media, Collections.emptyList())
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
    public ResponseEntity<?> updateProductReviewById(
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
    @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
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
            String message = localizationManager.localize("validation.product.create.empty_prices");
            SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        }

        if (createProductCommand.characteristics() == null || createProductCommand.characteristics().isEmpty()) {
            String message = localizationManager.localize("validation.product.create.empty_characteristics");
            SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        }

        if (productMedia == null || productMedia.isEmpty()) {
            String message = localizationManager.localize("validation.product.create.empty_product_media");
            SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        }

        ProductTitle productTitle = ProductTitle.of(createProductCommand.title());
        productTitle.setTranslations(new HstoreTranslationDto(
                createProductCommand.titleTranslations().en(),
                createProductCommand.titleTranslations().ru(),
                createProductCommand.titleTranslations().zh()
        ));

        ProductDescription productDescription = ProductDescription.of(
                createProductCommand.mainDescription(),
                createProductCommand.furtherDescription()
        );
        productDescription.setMainDescriptionTranslations(new HstoreTranslationDto(
                createProductCommand.mainDescriptionTranslations().en(),
                createProductCommand.mainDescriptionTranslations().ru(),
                createProductCommand.mainDescriptionTranslations().zh()
        ));
        productDescription.setFurtherDescriptionTranslations(new HstoreTranslationDto(
                createProductCommand.furtherDescriptionTranslations().en(),
                createProductCommand.furtherDescriptionTranslations().ru(),
                createProductCommand.furtherDescriptionTranslations().zh()
        ));

        CreateProduct operation = CreateProduct.of(
                securityUser,
                productTitle,
                productDescription,
                createProductCommand.categoryId(),
                createProductCommand.deliveryMethodIds(),
                createProductCommand.similarProducts() == null ? new ArrayList<>() : createProductCommand.similarProducts(),
                createProductCommand.prices(),
                createProductCommand.characteristics(),
                createProductCommand.faq() == null ? new ArrayList<>() : createProductCommand.faq(),
                createProductCommand.deliveryMethodDetails() == null ? new ArrayList<>() : createProductCommand.deliveryMethodDetails(),
                createProductCommand.packageOptions() == null ? new ArrayList<>() : createProductCommand.packageOptions(),
                createProductCommand.mediaAltTexts() == null ? new ArrayList<>() : createProductCommand.mediaAltTexts(),
                createProductCommand.minimumOrderQuantity(),
                ZonedDateTime.now().plusDays(createProductCommand.discountExpirationDate()),
                productMedia,
                productVendorDetailsMedia == null ? new ArrayList<>() : productVendorDetailsMedia
        );
        return productService.createProduct(operation).process(createProductProcessor);
    }

    @PutMapping(value = "{productId}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
    @Operation(
            summary = "Update product by ID",
            description = "Update the product with media files, prices, characteristics and FAQ",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product updated successfully",
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
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    content = @Content(
                            mediaType = MediaType.MULTIPART_FORM_DATA_VALUE,
                            schema = @Schema(
                                    type = "object",
                                    properties = {
                                            @StringToClassMapItem(
                                                    key = "data",
                                                    value = UpdateProductCommand.class
                                            ),
                                            @StringToClassMapItem(
                                                    key = "productMedia",
                                                    value = MultipartFile[].class
                                            ),
                                            @StringToClassMapItem(
                                                    key = "aboutVendorMedia",
                                                    value = MultipartFile[].class
                                            )
                                    }
                            )
                    )
            )
    )
    public ResponseEntity<?> updateProductById(
            @Parameter(
                    name = "productId",
                    description = "ID of the product to be retrieved",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable
            Long productId,

            @Parameter(
                    description = "Product data in JSON format",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = UpdateProductCommand.class)
                    )
            )
            @RequestPart("data") @Valid UpdateProductCommand updateProductCommand,

            @Parameter(
                    description = "Media files for the product (images, videos)",
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestPart(value = "productMedia", required = false) List<MultipartFile> productMedia,

            @Parameter(
                    description = "Media files for the product (images, videos)",
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestPart(value = "aboutVendorMedia", required = false) List<MultipartFile> productVendorDetailsMedia,

            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        if (updateProductCommand.prices() == null || updateProductCommand.prices().isEmpty()) {
            String message = localizationManager.localize("validation.product.update.empty_prices");
            SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        }

        if (updateProductCommand.characteristics() == null || updateProductCommand.characteristics().isEmpty()) {
            String message = localizationManager.localize("validation.product.update.empty_characteristics");
            SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        }

        if ((productMedia == null || productMedia.isEmpty()) && updateProductCommand.oldProductMedia().isEmpty()) {
            String message = localizationManager.localize("validation.product.update.empty_media");
            SimpleResponseErrorDto errorDto = SimpleResponseErrorDto.of(message, HttpStatus.BAD_REQUEST);
            return new ResponseEntity<>(errorDto, HttpStatus.BAD_REQUEST);
        }

        // Title
        ProductTitle productTitle = ProductTitle.of(Objects.requireNonNullElse(updateProductCommand.title(), ""));
        TranslationDto titleTranslations = Objects.requireNonNullElse(
                updateProductCommand.titleTranslations(),
                new TranslationDto(null, null, null)
        );
        productTitle.setTranslations(new HstoreTranslationDto(
                titleTranslations.en(),
                titleTranslations.ru(),
                titleTranslations.zh()
        ));

        // Description
        TranslationDto mainDescriptionTranslations = Objects.requireNonNullElse(
                updateProductCommand.mainDescriptionTranslations(),
                new TranslationDto(null, null, null)
        );
        TranslationDto furtherDescriptionTranslations = Objects.requireNonNullElse(
                updateProductCommand.furtherDescriptionTranslations(),
                new TranslationDto(null, null, null)
        );

        ProductDescription productDescription = ProductDescription.of(
                Objects.requireNonNullElse(updateProductCommand.mainDescription(), ""),
                Objects.requireNonNullElse(updateProductCommand.furtherDescription(), "")
        );
        productDescription.setMainDescriptionTranslations(new HstoreTranslationDto(
                mainDescriptionTranslations.en(),
                mainDescriptionTranslations.ru(),
                mainDescriptionTranslations.zh()
        ));
        productDescription.setFurtherDescriptionTranslations(new HstoreTranslationDto(
                furtherDescriptionTranslations.en(),
                furtherDescriptionTranslations.ru(),
                furtherDescriptionTranslations.zh()
        ));

        UpdateProduct operation = UpdateProduct.of(
                productId,
                securityUser,
                productTitle,
                productDescription,
                updateProductCommand.categoryId(),
                updateProductCommand.deliveryMethodIds(),
                updateProductCommand.similarProducts(),
                updateProductCommand.prices(),
                updateProductCommand.characteristics(),
                updateProductCommand.faq(),
                updateProductCommand.deliveryMethodDetails(),
                updateProductCommand.packageOptions(),
                updateProductCommand.mediaAltTexts(),
                updateProductCommand.minimumOrderQuantity(),
                ZonedDateTime.now().plusDays(updateProductCommand.discountExpirationDate()),
                updateProductCommand.oldProductMedia(),
                updateProductCommand.oldAboutVendorMedia(),
                Objects.requireNonNullElse(productMedia, new ArrayList<>()),
                Objects.requireNonNullElse(productVendorDetailsMedia, new ArrayList<>())
        );
        return productService.updateProduct(operation).process(updateProductProcessor);
    }

    @GetMapping("hints")
    @Operation(
            summary = "Get product search hints",
            description = "Retrieve product search hints grouped by category based on the search text. " +
                    "Returns a list of categories with their matching products for autocomplete functionality.",
            parameters = {
                    @Parameter(
                            name = "text",
                            description = "Search text to find matching products. If not provided or empty, " +
                                    "default value '$$$' is used which may return no results.",
                            example = "смартфон",
                            schema = @Schema(type = "string", minLength = 1, maxLength = 100)
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Search hints retrieved successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            type = "array",
                                            implementation = SearchHintDto.class,
                                            description = "List of categories with their matching products"
                                    ),
                                    examples = @ExampleObject(
                                            name = "search_hints_example",
                                            summary = "Example search hints response",
                                            value = """
                                                    [
                                                        {
                                                            "category": "Электроника",
                                                            "products": [
                                                                {
                                                                    "id": 1,
                                                                    "title": "Смартфон Apple iPhone 14"
                                                                },
                                                                {
                                                                    "id": 2,
                                                                    "title": "Смартфон Samsung Galaxy S23"
                                                                }
                                                            ]
                                                        },
                                                        {
                                                            "category": "Аксессуары",
                                                            "products": [
                                                                {
                                                                    "id": 15,
                                                                    "title": "Чехол для смартфона"
                                                                }
                                                            ]
                                                        }
                                                    ]
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid search parameters",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            },
            tags = {"Products", "Search"}
    )
    public ResponseEntity<?> getSearchHints(
            @RequestParam(required = false, defaultValue = "$$$") String text
    ) {
        GetSearchHints operation = GetSearchHints.of(text);
        return productService.getSearchHints(operation).process(getSearchHintsProcessor);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Delete product by ID",
            description = "Permanently deletes a product and all its associated media files from the system. " +
                    "Operation sequence: 1) Delete all media files 2) Delete product record. " +
                    "This operation is irreversible and requires ADMIN privileges.",
            parameters = {
                    @Parameter(
                            name = "id",
                            description = "Unique identifier of the product to delete",
                            required = true,
                            example = "123",
                            schema = @Schema(type = "integer", format = "int64", minimum = "1")
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product deleted successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseMessageDto.class,
                                            description = "Success confirmation message"
                                    ),
                                    examples = @ExampleObject(
                                            name = "success_response",
                                            summary = "Success response example",
                                            value = """
                                                    {
                                                        "message": "Product deleted successfully"
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class),
                                    examples = @ExampleObject(
                                            name = "not_found_response",
                                            summary = "Not found response example",
                                            value = """
                                                    {
                                                        "error": "Not Found",
                                                        "message": "Product with ID '123' not found",
                                                        "status": 404
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class),
                                    examples = @ExampleObject(
                                            name = "forbidden_response",
                                            summary = "Forbidden response example",
                                            value = """
                                                    {
                                                        "error": "Forbidden",
                                                        "message": "Access denied",
                                                        "status": 403
                                                    }
                                                    """
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error during deletion",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class),
                                    examples = @ExampleObject(
                                            name = "error_response",
                                            summary = "Error response example",
                                            value = """
                                                    {
                                                        "error": "Internal Server Error",
                                                        "message": "Failed to delete product media files",
                                                        "status": 500
                                                    }
                                                    """
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> deleteProductById(@PathVariable Long id) {
        DeleteProductById operation = DeleteProductById.of(id);
        return productService.deleteProductById(operation).process(deleteProductByIdProcessor);
    }
}
