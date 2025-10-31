package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.command.me.RefreshMeCurrentSessionCommand;
import com.surofu.exporteru.application.command.me.UpdateMeCommand;
import com.surofu.exporteru.application.command.me.VerifyDeleteMeCommand;
import com.surofu.exporteru.application.dto.SimpleResponseMessageDto;
import com.surofu.exporteru.application.dto.TokenDto;
import com.surofu.exporteru.application.dto.UserDto;
import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.application.dto.error.ValidationExceptionDto;
import com.surofu.exporteru.application.dto.me.GetMeProductReviewPageDto;
import com.surofu.exporteru.application.dto.me.GetMeVendorProductReviewPageDto;
import com.surofu.exporteru.application.dto.product.GetProductSummaryViewPageDto;
import com.surofu.exporteru.application.dto.session.SessionDto;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.core.model.moderation.ApproveStatus;
import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.core.model.user.UserPhoneNumber;
import com.surofu.exporteru.core.model.user.UserRegion;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsAddress;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsDescription;
import com.surofu.exporteru.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.exporteru.core.model.vendorDetails.country.VendorCountryName;
import com.surofu.exporteru.core.model.vendorDetails.email.VendorEmailEmail;
import com.surofu.exporteru.core.model.vendorDetails.phoneNumber.VendorPhoneNumberPhoneNumber;
import com.surofu.exporteru.core.model.vendorDetails.productCategory.VendorProductCategoryName;
import com.surofu.exporteru.core.model.vendorDetails.site.VendorSiteUrl;
import com.surofu.exporteru.core.service.me.MeService;
import com.surofu.exporteru.core.service.me.operation.*;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/me")
@Tag(
        name = "Current User Profile",
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
    private final DeleteMeSessionById.Result.Processor<ResponseEntity<?>> deleteMeSessionByIdProcessor;
    private final SaveMeAvatar.Result.Processor<ResponseEntity<?>> saveMeAvatarProcessor;
    private final DeleteMeAvatar.Result.Processor<ResponseEntity<?>> deleteMeAvatarProcessor;
    private final DeleteMe.Result.Processor<ResponseEntity<?>> deleteMeProcessor;
    private final VerifyDeleteMe.Result.Processor<ResponseEntity<?>> verifyDeleteMeProcessor;
    private final UploadMeVendorMedia.Result.Processor<ResponseEntity<?>> uploadMeVendorMediaProcessor;
    private final DeleteMeVendorMediaById.Result.Processor<ResponseEntity<?>> deleteMeVendorMediaByIdProcessor;
    private final DeleteMeVendorMediaByIdList.Result.Processor<ResponseEntity<?>> deleteMeVendorMediaByIdListProcessor;
    private final DeleteMeReviewById.Result.Processor<ResponseEntity<?>> deleteMeReviewByIdProcessor;

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
            @Valid @RequestBody UpdateMeCommand command,

            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser) {
        Locale locale = LocaleContextHolder.getLocale();
        UpdateMe operation = UpdateMe.of(
                securityUser,
                command.login() != null ? UserLogin.of(command.login()) : null,
                command.phoneNumber() != null ? UserPhoneNumber.of(command.phoneNumber()) : null,
                command.region() != null ? UserRegion.of(command.region()) : null,
                command.inn() != null ? VendorDetailsInn.of(command.inn()) : null,
                command.address() != null ? VendorDetailsAddress.of(command.address()) : null,
                command.description() != null ? VendorDetailsDescription.of(command.description()) : null,
                command.countries() != null ? command.countries().stream().map(VendorCountryName::of).toList() : null,
                command.categories() != null ? command.categories().stream().map(VendorProductCategoryName::of).toList() : null,
                command.phoneNumbers() != null ? command.phoneNumbers().stream().map(VendorPhoneNumberPhoneNumber::of).toList() : null,
                command.emails() != null ? command.emails().stream().map(VendorEmailEmail::of).toList() : null,
                command.sites() != null ? command.sites().stream().map(VendorSiteUrl::of).toList() : null,
                locale
        );
        return meService.updateMe(operation).process(updateMeProcessor);
    }

    @GetMapping("reviews")
    @PreAuthorize("isAuthenticated()")
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
            @AuthenticationPrincipal SecurityUser securityUser,
            @AuthenticationPrincipal Object principal
    ) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        Locale locale = LocaleContextHolder.getLocale();
        GetMeReviewPage operation = GetMeReviewPage.of(securityUser, page, size, minRating, maxRating, locale);
        return meService.getMeReviewPage(operation).process(getMeReviewsProcessor);
    }

    @DeleteMapping("reviews/{id}")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Delete review by ID, that has been left by current user",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully deleted product review",
                            content = @Content(
                                    mediaType = "application/json",
                                    array = @ArraySchema(schema = @Schema(implementation = SimpleResponseMessageDto.class))
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "Review not found",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Unknown error",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> deleteMeReviewById(@PathVariable Long id, @AuthenticationPrincipal SecurityUser securityUser) {
        DeleteMeReviewById operation = DeleteMeReviewById.of(id, securityUser);
        return meService.deleteMeReviewById(operation).process(deleteMeReviewByIdProcessor);
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
        Locale locale = LocaleContextHolder.getLocale();
        GetMeVendorProductReviewPage operation = GetMeVendorProductReviewPage.of(securityUser, page, size, minRating, maxRating, locale);
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

            @RequestParam(required = false, defaultValue = "id")
            String sort,

            @RequestParam(required = false, defaultValue = "asc")
            String direction,

            @RequestParam(required = false)
            List<ApproveStatus> approveStatuses,

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
                maxPrice,
                sort,
                direction,
                Objects.requireNonNullElse(approveStatuses, new ArrayList<>())
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
        return meService.deleteMeSessionById(operation).process(deleteMeSessionByIdProcessor);
    }

    @PutMapping(value = "avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Upload or update user avatar",
            description = "Allows authenticated users to upload or update their profile picture",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Avatar uploaded successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid file format or size",
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
                            responseCode = "500",
                            description = "Internal server error during avatar processing",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> saveAvatar(
            @Parameter(
                    description = "Image file to upload (JPEG/PNG, max 20MB)",
                    required = true,
                    content = @Content(mediaType = "multipart/form-data")
            )
            @RequestPart("file") MultipartFile file,

            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        SaveMeAvatar operation = SaveMeAvatar.of(file, securityUser);
        return meService.saveMeAvatar(operation).process(saveMeAvatarProcessor);
    }

    @DeleteMapping("avatar")
    @PreAuthorize("isAuthenticated()")
    @Operation(
            summary = "Remove user avatar",
            description = "Allows authenticated users to delete their profile picture",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Avatar deleted successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
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
                            responseCode = "404",
                            description = "No avatar exists to delete",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "500",
                            description = "Internal server error during avatar deletion",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> deleteAvatar(
            @Parameter(hidden = true)
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        DeleteMeAvatar operation = DeleteMeAvatar.of(securityUser);
        return meService.deleteMeAvatar(operation).process(deleteMeAvatarProcessor);
    }

    @DeleteMapping("delete-account")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> deleteMe(@AuthenticationPrincipal SecurityUser securityUser) {
        Locale locale = LocaleContextHolder.getLocale();
        DeleteMe operation = DeleteMe.of(securityUser, locale);
        return meService.deleteMe(operation).process(deleteMeProcessor);
    }

    @DeleteMapping("verify-delete-account")
    @PreAuthorize("isAuthenticated()")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> verifyMe(@AuthenticationPrincipal SecurityUser securityUser,
                                      @RequestBody VerifyDeleteMeCommand command) {
        Locale locale = LocaleContextHolder.getLocale();
        VerifyDeleteMe operation = VerifyDeleteMe.of(securityUser, command.code(), locale);
        return meService.verifyDeleteMe(operation).process(verifyDeleteMeProcessor);
    }

    @PostMapping(value = "vendor/media", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> uploadMeVendorMedia(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestPart("media") List<MultipartFile> media,
            @RequestPart("oldMediaIds") List<Long> oldMediaIds,
            @RequestPart("newMediaPositions") List<Integer> newMediaPositions
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        UploadMeVendorMedia operation = UploadMeVendorMedia.of(securityUser, media, oldMediaIds, newMediaPositions, locale);
        return meService.uploadMeVendorMedia(operation).process(uploadMeVendorMediaProcessor);
    }

    @DeleteMapping("vendor/media/{id}")
    @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> deleteMeVendorMediaById(
            @AuthenticationPrincipal SecurityUser securityUser,
            @PathVariable Long id
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        DeleteMeVendorMediaById operation = DeleteMeVendorMediaById.of(securityUser, id, locale);
        return meService.deleteMeVendorMediaById(operation).process(deleteMeVendorMediaByIdProcessor);
    }

    @DeleteMapping("vendor/media")
    @PreAuthorize("hasAnyRole('ROLE_VENDOR', 'ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> deleteMeVendorMedia(
            @AuthenticationPrincipal SecurityUser securityUser,
            @RequestBody List<Long> ids
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        DeleteMeVendorMediaByIdList operation = DeleteMeVendorMediaByIdList.of(securityUser, Objects.requireNonNullElse(ids, new ArrayList<>()), locale);
        return meService.deleteMeVendorMediaByIdList(operation).process(deleteMeVendorMediaByIdListProcessor);
    }
}