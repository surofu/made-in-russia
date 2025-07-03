package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.command.vendor.CreateVendorFaqCommand;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.VendorDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.dto.error.ValidationExceptionDto;
import com.surofu.madeinrussia.application.dto.page.GetProductSummaryViewPageDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaqAnswer;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorFaq.VendorFaqQuestion;
import com.surofu.madeinrussia.core.service.product.ProductSummaryService;
import com.surofu.madeinrussia.core.service.product.operation.GetProductSummaryViewPageByVendorId;
import com.surofu.madeinrussia.core.service.vendor.VendorService;
import com.surofu.madeinrussia.core.service.vendor.operation.CreateVendorFaq;
import com.surofu.madeinrussia.core.service.vendor.operation.DeleteVendorFaqById;
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
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

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
    private final CreateVendorFaq.Result.Processor<ResponseEntity<?>> createVendorFaqProcessor;
    private final DeleteVendorFaqById.Result.Processor<ResponseEntity<?>> deleteVendorFaqProcessor;

    @GetMapping("{vendorId}")
    @Operation(
            summary = "Get vendor information by ID",
            description = "Returns complete information of the vendor",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved vendor information",
                            content = @Content(
                                    schema = @Schema(implementation = VendorDto.class)
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
            @PathVariable Long vendorId,
            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser) {
        GetVendorById operation = GetVendorById.of(Optional.ofNullable(securityUser), vendorId);
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

    @PostMapping("faq")
    @PreAuthorize("hasAnyRole('ROLE_VENDOR')")
    @Operation(
            summary = "Create a new vendor FAQ",
            description = "Creates a new frequently asked question and answer pair for the authenticated vendor",
            security = @SecurityRequirement(name = "bearerAuth"),
            responses = {
                    @ApiResponse(
                            responseCode = "201",
                            description = "Successfully created vendor FAQ",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters or validation errors",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = ValidationExceptionDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - authentication required",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions (ROLE_VENDOR required)",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> createVendorFaq(
            @Parameter(
                    description = "Vendor FAQ creation request containing question and answer",
                    required = true,
                    schema = @Schema(implementation = CreateVendorFaqCommand.class)
            )
            @RequestBody @Valid CreateVendorFaqCommand createVendorFaqCommand,

            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        CreateVendorFaq operation = CreateVendorFaq.of(
                securityUser,
                VendorFaqQuestion.of(createVendorFaqCommand.question()),
                VendorFaqAnswer.of(createVendorFaqCommand.answer())
        );
        return vendorService.createVendorFaq(operation).process(createVendorFaqProcessor);
    }

    @DeleteMapping("faq/{faqId}")
    @PreAuthorize("hasAnyRole('ROLE_VENDOR')")
    public ResponseEntity<?> deleteVendorFaq(
            @PathVariable Long faqId,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        DeleteVendorFaqById operation = DeleteVendorFaqById.of(securityUser, faqId);
        return vendorService.deleteVendorFaqById(operation).process(deleteVendorFaqProcessor);
    }
}
