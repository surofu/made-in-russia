package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.command.me.RefreshMeCurrentSessionCommand;
import com.surofu.madeinrussia.application.command.me.UpdateMeCommand;
import com.surofu.madeinrussia.application.dto.SessionDto;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.TokenDto;
import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.dto.error.ValidationExceptionDto;
import com.surofu.madeinrussia.application.dto.page.GetMeProductReviewPageDto;
import com.surofu.madeinrussia.application.dto.page.GetMeVendorProductReviewPageDto;
import com.surofu.madeinrussia.application.dto.page.GetProductSummaryViewPageDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import com.surofu.madeinrussia.core.model.user.UserRegion;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategoryName;
import com.surofu.madeinrussia.core.service.me.MeService;
import com.surofu.madeinrussia.core.service.me.operation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.Explode;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/me")
@Tag(
        name = "User Profile",
        description = "API for accessing current user information and session management"
)
public class MeRestController {
    private final MeService meService;

    private final GetMe.Result.Processor<ResponseEntity<?>> getMeByJwtProcessor;
    private final GetMeSessions.Result.Processor<ResponseEntity<?>> getMeSessionsProcessor;
    private final GetMeCurrentSession.Result.Processor<ResponseEntity<?>> getMeCurrentSessionProcessor;
    private final RefreshMeCurrentSession.Result.Processor<ResponseEntity<?>> refreshMeCurrentSessionProcessor;
    private final UpdateMe.Result.Processor<ResponseEntity<?>> updateMeProcessor;
    private final GetMeReviewPage.Result.Processor<ResponseEntity<?>> getMeReviewsProcessor;
    private final GetMeVendorProductReviewPage.Result.Processor<ResponseEntity<?>> getMeVendorProductReviewPageProcessor;
    private final GetMeProductSummaryViewPage.Result.Processor<ResponseEntity<?>> getMeProductSummaryViewPageProcessor;
    private final DeleteMeSessionById.Result.Processor<ResponseEntity<?>> getDeleteMeSessionByIdProcessor;

    @GetMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get current user profile",
            description = """
                    Returns complete profile information for the authenticated user.
                    Requires USER role.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user profile",
                            content = @Content(
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or missing JWT token",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions",
                            content = @Content
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMeByJwt(@Parameter(hidden = true)
                                        @AuthenticationPrincipal SecurityUser securityUser) {
        Locale locale = LocaleContextHolder.getLocale();
        GetMe operation = GetMe.of(securityUser, locale);
        return meService.getMeByJwt(operation).process(getMeByJwtProcessor);
    }

    @GetMapping("sessions")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get user sessions",
            description = """
                    Returns list of all active sessions for the authenticated user.
                    Includes device and location information.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved sessions",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SessionDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or missing JWT token",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions",
                            content = @Content
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMeSessions(@Parameter(hidden = true)
                                           @AuthenticationPrincipal SecurityUser securityUser) {
        GetMeSessions operation = GetMeSessions.of(securityUser);
        return meService.getMeSessions(operation).process(getMeSessionsProcessor);
    }

    @GetMapping("current-session")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get current session details",
            description = """
                    Returns detailed information about the current authentication session.
                    Includes IP address, device info and last activity.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved session",
                            content = @Content(
                                    schema = @Schema(implementation = SessionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or missing JWT token",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions",
                            content = @Content
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMeCurrentSession(
            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser) {
        GetMeCurrentSession operation = GetMeCurrentSession.of(securityUser);
        return meService.getMeCurrentSession(operation).process(getMeCurrentSessionProcessor);
    }

    @PatchMapping("current-session/refresh")
    @Operation(
            summary = "Refresh current session",
            description = """
                    Extends the expiration time of the current session.
                    Requires valid refresh token.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Session refreshed successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = TokenDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid refresh token",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ValidationExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or expired refresh token",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<?> refreshMeCurrentSessionRefresh(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Refresh token payload",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RefreshMeCurrentSessionCommand.class),
                            examples = {
                                    @ExampleObject(
                                            value = """
                                                    {
                                                      "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
                                                    }"""
                                    )
                            }
                    )
            )
            @Valid @RequestBody RefreshMeCurrentSessionCommand refreshMeCurrentSessionCommand,
            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        SessionInfo sessionInfo = SessionInfo.of(request);
        RefreshMeCurrentSession operation = RefreshMeCurrentSession.of(
                sessionInfo,
                refreshMeCurrentSessionCommand.refreshToken()
        );
        return meService.refreshMeCurrentSession(operation).process(refreshMeCurrentSessionProcessor);
    }

    @PatchMapping
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Update user profile",
            description = """
                    Updates profile information for the authenticated user.
                    Only the region field can be updated through this endpoint.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Profile updated successfully",
                            content = @Content(
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(
                                    schema = @Schema(implementation = ValidationExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or missing JWT token",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions",
                            content = @Content
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMe(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User profile update data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = UpdateMeCommand.class),
                            examples = {
                                    @ExampleObject(
                                            description = "For User",
                                            value = """
                                                    {
                                                      "phoneNumber": "+375281234567",
                                                      "region": "Russia"
                                                    }"""
                                    ),
                                    @ExampleObject(
                                            description = "For User",
                                            value = """
                                                    {
                                                      "region": "China"
                                                    }"""
                                    ),
                                    @ExampleObject(
                                            description = "For Vendor",
                                            value = """
                                                    {
                                                      "phoneNumber": "+375281234567",
                                                      "inn": "123456789",
                                                      "countries": ["Russia", "China"],
                                                      "categories": ["Wood", "Coal"]
                                                    }"""
                                    ),
                                    @ExampleObject(
                                            description = "For Vendor",
                                            value = """
                                                    {
                                                      "inn": "123456789",
                                                      "countries": ["Russia", "China"],
                                                    }"""
                                    ),
                                    @ExampleObject(
                                            description = "For Vendor",
                                            value = """
                                                    {
                                                      "categories": ["Wood", "Coal"]
                                                    }"""
                                    ),
                            }
                    )
            )
            @Valid @RequestBody UpdateMeCommand updateMeCommand,

            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser) {
        UpdateMe operation = UpdateMe.of(
                securityUser,
                updateMeCommand.phoneNumber() != null ? UserPhoneNumber.of(updateMeCommand.phoneNumber()) : null,
                updateMeCommand.region() != null ? UserRegion.of(updateMeCommand.region()) : null,
                updateMeCommand.inn() != null ? VendorDetailsInn.of(updateMeCommand.inn()) : null,
                updateMeCommand.countries() != null ? updateMeCommand.countries().stream().map(VendorCountryName::of).toList() : null,
                updateMeCommand.categories() != null ? updateMeCommand.categories().stream().map(VendorProductCategoryName::of).toList() : null
        );
        return meService.updateMe(operation).process(updateMeProcessor);
    }

    @GetMapping("reviews")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get user reviews",
            description = """
                    Returns paginated list of reviews left by the current user.
                    Supports filtering by rating range.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved reviews",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = GetMeProductReviewPageDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid pagination parameters",
                            content = @Content(
                                    schema = @Schema(implementation = ValidationExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or missing JWT token",
                            content = @Content
                    )
            }
    )
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> getMeReviewPage(
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
            Integer maxRating,

            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser) {
        GetMeReviewPage operation = GetMeReviewPage.of(securityUser, page, size, minRating, maxRating);
        return meService.getMeReviewPage(operation).process(getMeReviewsProcessor);
    }

    @GetMapping("product-reviews")
    @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get user product reviews",
            description = """
                    Returns paginated list of product reviews left by the current user.
                    Supports filtering by rating range.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved product reviews",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = GetMeVendorProductReviewPageDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid pagination parameters",
                            content = @Content(
                                    schema = @Schema(implementation = ValidationExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or missing JWT token",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<?> getMeVendorProductReviewPage(
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
            Integer maxRating,

            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser) {
        GetMeVendorProductReviewPage operation = GetMeVendorProductReviewPage.of(securityUser, page, size, minRating, maxRating);
        return meService.getMeVendorProductReviewPage(operation).process(getMeVendorProductReviewPageProcessor);
    }

    @GetMapping("products-summary")
    @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Get paginated product summary view",
            description = """
                    Returns paginated list of product summaries with detailed information including:
                    - Product details (title, price, rating, etc.)
                    - Associated user information
                    - Category details
                    - Available delivery methods
                    Supports filtering by multiple criteria including title, categories, delivery methods and price range.
                    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved product summaries",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = GetProductSummaryViewPageDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content(
                                    schema = @Schema(implementation = ValidationExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or missing JWT token",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<?> getMeProductSummaryViewPage(
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
                    description = "Filter products by title (case-insensitive contains)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "string", example = "smartphone")
            )
            @RequestParam(required = false)
            String title,

            @Parameter(
                    name = "deliveryMethodIds",
                    description = "Filter products by delivery method IDs. Multiple IDs can be provided as comma-separated values",
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
                    description = "Filter products by category IDs. Multiple IDs can be provided as comma-separated values",
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
                    description = "Minimum discounted price filter (inclusive)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(
                            type = "number",
                            format = "decimal",
                            example = "100.50",
                            minimum = "0"
                    )
            )
            @RequestParam(required = false)
            @DecimalMin("0")
            BigDecimal minPrice,

            @Parameter(
                    name = "maxPrice",
                    description = "Maximum discounted price filter (inclusive)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(
                            type = "number",
                            format = "decimal",
                            example = "999.99",
                            minimum = "0"
                    )
            )
            @RequestParam(required = false)
            @DecimalMin("0")
            BigDecimal maxPrice,

            @Parameter(hidden = true)
            @AuthenticationPrincipal
            SecurityUser securityUser
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        GetMeProductSummaryViewPage operation = GetMeProductSummaryViewPage.of(
                locale,
                securityUser,
                page,
                size,
                title,
                deliveryMethodIds,
                categoryIds,
                minPrice,
                maxPrice
        );

        return meService.getMeProductSummaryViewPage(operation).process(getMeProductSummaryViewPageProcessor);
    }

    @DeleteMapping("sessions/{sessionId}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Delete current user session by ID",
            description = "Delete current user session by ID, authentication required",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted session",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SimpleResponseMessageDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - invalid or missing JWT token",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<?> deleteSessionById(
            @Parameter(
                    name = "sessionId",
                    description = "ID of the user session to be deleted",
                    required = true,
                    example = "20",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable Long sessionId,

            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        DeleteMeSessionById operation = DeleteMeSessionById.of(
                securityUser,
                sessionId
        );
        return meService.deleteMeSessionById(operation).process(getDeleteMeSessionByIdProcessor);
    }
}