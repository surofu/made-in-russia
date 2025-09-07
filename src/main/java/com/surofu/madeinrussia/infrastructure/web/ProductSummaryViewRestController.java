package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.dto.error.ValidationExceptionDto;
import com.surofu.madeinrussia.application.dto.product.GetProductSummaryViewPageDto;
import com.surofu.madeinrussia.application.dto.product.ProductSummaryViewDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.moderation.ApproveStatus;
import com.surofu.madeinrussia.core.service.product.ProductSummaryService;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewById;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewPage;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewsByIds;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/products-summary")
@Tag(name = "Products Summary", description = "API for managing product summary")
public class ProductSummaryViewRestController {

    private final ProductSummaryService productSummaryService;

    private final GetProductSummaryViewPage.Result.Processor<ResponseEntity<?>> getProductSummaryPageProcessor;
    private final GetProductSummaryViewsByIds.Result.Processor<ResponseEntity<?>> getProductSummaryViewsByIdsProcessor;
    private final GetProductSummaryViewById.Result.Processor<ResponseEntity<?>> getProductSummaryByIdProcessor;

    @GetMapping
    @Operation(
            summary = "Get filtered and paginated list of products summary",
            description = "Retrieves a page of products with optional filtering by category and price range",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved page of products summary",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = GetProductSummaryViewPageDto.class)
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
            @RequestParam(defaultValue = "")
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
            BigDecimal maxPrice,

            @RequestParam(required = false)
            List<ApproveStatus> approveStatuses,

            @RequestParam(required = false, defaultValue = "id")
            String sort,

            @RequestParam(required = false, defaultValue = "asc")
            String direction,

            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        GetProductSummaryViewPage operation = GetProductSummaryViewPage.of(
                locale,
                page,
                size,
                title,
                deliveryMethodIds,
                categoryIds,
                minPrice,
                maxPrice,
                Objects.requireNonNullElse(approveStatuses, new ArrayList<>()),
                sort,
                direction,
                securityUser
        );
        return productSummaryService.getProductSummaryPage(operation).process(getProductSummaryPageProcessor);
    }

    @GetMapping("ids")
    public ResponseEntity<?> getProductsByIds(
            @Parameter(
                    name = "ids",
                    description = "Find product summaries by it's ids",
                    in = ParameterIn.QUERY,
                    schema = @Schema(
                            type = "array",
                            format = "int64",
                            example = "[1, 2, 3]",
                            minLength = 1,
                            maxLength = 100
                    ),
                    explode = Explode.FALSE,
                    examples = {
                            @ExampleObject(
                                    name = "Single product id",
                                    value = "1",
                                    description = "Filter by single product ID"
                            ),
                            @ExampleObject(
                                    name = "Multiple product ids",
                                    value = "1,2,3",
                                    description = "Find by multiple product IDs"
                            )
                    }
            )
            @RequestParam
            List<Long> ids
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        GetProductSummaryViewsByIds operation = GetProductSummaryViewsByIds.of(locale, ids);
        return productSummaryService.getProductSummaryViewsByIds(operation).process(getProductSummaryViewsByIdsProcessor);
    }

    @GetMapping("{productId}")
    @Operation(
            summary = "Get product summary by ID",
            description = "Retrieves a single product by its unique identifier",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Product found and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ProductSummaryViewDto.class)
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
        Locale locale = LocaleContextHolder.getLocale();
        GetProductSummaryViewById operation = GetProductSummaryViewById.of(locale, productId);
        return productSummaryService.getProductSummaryViewById(operation).process(getProductSummaryByIdProcessor);
    }
}
