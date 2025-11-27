package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.dto.SimpleResponseMessageDto;
import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.dto.error.ValidationExceptionDto;
import com.surofu.exporteru.application.dto.product.GetProductReviewPageDto;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.service.productReview.ProductReviewService;
import com.surofu.exporteru.core.service.productReview.operation.DeleteProductReviewById;
import com.surofu.exporteru.core.service.productReview.operation.GetProductReviewPage;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/product-reviews")
@Tag(
        name = "Product Reviews",
        description = "API for accessing product reviews"
)
public class ProductReviewRestController {
    private final ProductReviewService productReviewService;

    private final GetProductReviewPage.Result.Processor<ResponseEntity<?>> getProductReviewPageProcessor;
    private final DeleteProductReviewById.Result.Processor<ResponseEntity<?>> deleteProductReviewByIdProcessor;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get page of product reviews (Admin only)",
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
    public ResponseEntity<?> getProductReviewPage(
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
                    name = "commentText",
                    description = "Review text",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "string", example = "Hello world!")
            )
            @RequestParam(name = "commentText", required = false)
            String content,

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
            Integer maxRating,

            @RequestParam(required = false)
            List<ApproveStatus> approveStatuses,

            @RequestParam(required = false, defaultValue = "id")
            String sort,

            @RequestParam(required = false, defaultValue = "asc")
            String direction
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        GetProductReviewPage operation = GetProductReviewPage.of(
                page, size, content, minRating, maxRating,
                Objects.requireNonNullElse(approveStatuses, new ArrayList<>()),
                sort,
                direction,
                locale
        );
        return productReviewService.getProductReviewPage(operation).process(getProductReviewPageProcessor);
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Delete product review by id (Admin only)",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted product review",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
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
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Product review with such ID not found",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal Server Error",
                            content = @Content(
                                    schema = @Schema(
                                            implementation = SimpleResponseErrorDto.class
                                    )
                            )
                    )
            }
    )
    public ResponseEntity<?> deleteProductReviewById(
            @Parameter(
                    name = "id",
                    description = "ID of the product review that will be deleted",
                    required = true,
                    example = "123",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable(name = "id") Long id) {
        DeleteProductReviewById operation = DeleteProductReviewById.of(id);
        return productReviewService.deleteProductReviewById(operation).process(deleteProductReviewByIdProcessor);
    }
}
