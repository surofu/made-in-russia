package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.service.vendor.VendorService;
import com.surofu.madeinrussia.core.service.vendor.operation.GetVendorById;
import com.surofu.madeinrussia.core.service.vendor.operation.GetVendorReviewPageById;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/vendor")
@Tag(
        name = "Vendor Profile",
        description = "API for accessing vendor information"
)
public class VendorRestController {

    private final VendorService vendorService;

    private final GetVendorById.Result.Processor<ResponseEntity<?>> getVendorByIdProcessor;
    private final GetVendorReviewPageById.Result.Processor<ResponseEntity<?>> getVendorReviewPageByIdProcessor;

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
