package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.dto.ValidationExceptionDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.dto.page.GetProductSummaryViewPageDto;
import com.surofu.madeinrussia.core.service.product.ProductSummaryService;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewPageByVendorId;
import com.surofu.madeinrussia.core.service.vendor.VendorService;
import com.surofu.madeinrussia.core.service.vendor.operation.GetVendorById;
import com.surofu.madeinrussia.core.service.vendor.operation.GetVendorReviewPageById;
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
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vendor")
@Tag(
        name = "Vendor Profile",
        description = "API for accessing vendor information"
)
public class VendorRestController {

    private final VendorService vendorService;
    private final ProductSummaryService productSummaryService;

    private final GetVendorById.Result.Processor<ResponseEntity<?>> getVendorByIdProcessor;
    private final GetVendorReviewPageById.Result.Processor<ResponseEntity<?>> getVendorReviewPageByIdProcessor;
    private final GetProductSummaryViewPageByVendorId.Result.Processor<ResponseEntity<?>> getProductSummaryViewPageByVendorIdProcessor;

    @GetMapping("{vendorId}")
    @Operation(
            summary = "Get vendor information by ID",
            description = "Returns complete information of the vendor",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved vendor information",
                            content = @Content(
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> getVendorById(
            @Parameter(
                    name = "vendorId",
                    description = "ID of the vendor to be retrieved",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long vendorId) {
        GetVendorById operation = GetVendorById.of(vendorId);
        return vendorService.getVendorById(operation).process(getVendorByIdProcessor);
    }

    @GetMapping("{vendorId}/products-summary")
    @Operation(
            summary = "Get filtered and paginated list of vendor products summary",
            description = "Retrieves a page of vendor products with optional filtering by category and price range",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved page of vendor products summary",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = GetProductSummaryViewPageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
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
    public ResponseEntity<?> getProductSummary(
            @Parameter(
                    name = "vendorId",
                    description = "ID of the vendor to be retrieved",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long vendorId,

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
            BigDecimal maxPrice
    ) {
        GetProductSummaryViewPageByVendorId operation = GetProductSummaryViewPageByVendorId.of(
                vendorId,
                page,
                size,
                title,
                categoryIds,
                deliveryMethodIds,
                minPrice,
                maxPrice
        );
        return productSummaryService.getProductSummaryViewPageByVendorId(operation).process(getProductSummaryViewPageByVendorIdProcessor);
    }

    @GetMapping("{vendorId}/reviews")
    public ResponseEntity<?> getVendorReviews(
            @Parameter(
                    name = "vendorId",
                    description = "ID of the vendor to be retrieved",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long vendorId,

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
        GetVendorReviewPageById operation = GetVendorReviewPageById.of(
                vendorId,
                page,
                size,
                minRating,
                maxRating
        );

        return vendorService.getVendorReviewPageById(operation).process(getVendorReviewPageByIdProcessor);
    }
}
