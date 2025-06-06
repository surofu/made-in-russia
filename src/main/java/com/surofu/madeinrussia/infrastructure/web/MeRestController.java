package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.command.me.RefreshMeCurrentSessionCommand;
import com.surofu.madeinrussia.application.command.me.UpdateMeCommand;
import com.surofu.madeinrussia.application.dto.SessionDto;
import com.surofu.madeinrussia.application.dto.TokenDto;
import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.dto.ValidationExceptionDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.core.model.user.UserRegion;
import com.surofu.madeinrussia.core.service.me.MeService;
import com.surofu.madeinrussia.core.service.me.operation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
    public ResponseEntity<?> getMeByJwt(@AuthenticationPrincipal SecurityUser securityUser) {
        GetMe operation = GetMe.of(securityUser);
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
    public ResponseEntity<?> getMeSessions(@AuthenticationPrincipal SecurityUser securityUser) {
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
    public ResponseEntity<?> getMeCurrentSession(@AuthenticationPrincipal SecurityUser securityUser) {
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
            @Valid @RequestBody RefreshMeCurrentSessionCommand refreshMeCurrentSessionCommand
    ) {
        RefreshMeCurrentSession operation = RefreshMeCurrentSession.of(refreshMeCurrentSessionCommand);
        return meService.refreshMeCurrentSession(operation).process(refreshMeCurrentSessionProcessor);
    }

    @PatchMapping()
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<?> updateMe(@AuthenticationPrincipal SecurityUser securityUser, @RequestBody UpdateMeCommand updateMeCommand) {
        UpdateMe operation = UpdateMe.of(securityUser, UserRegion.of(updateMeCommand.region()));
        return meService.updateMe(operation).process(updateMeProcessor);
    }

    @GetMapping("reviews")
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
            Integer minRating,

            @Parameter(
                    name = "maxRating",
                    description = "Maximum product review rating filter (inclusive)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "number", example = "10000")
            )
            Integer maxRating,
            @AuthenticationPrincipal SecurityUser securityUser) {
        GetMeReviewPage operation = GetMeReviewPage.of(securityUser, page, size, minRating, maxRating);
        return meService.getMeReviewPage(operation).process(getMeReviewsProcessor);
    }

    @GetMapping("product-reviews")
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
            Integer minRating,

            @Parameter(
                    name = "maxRating",
                    description = "Maximum product review rating filter (inclusive)",
                    in = ParameterIn.QUERY,
                    schema = @Schema(type = "number", example = "10000")
            )
            Integer maxRating,
            @AuthenticationPrincipal SecurityUser securityUser) {
        GetMeVendorProductReviewPage operation = GetMeVendorProductReviewPage.of(securityUser, page, size, minRating, maxRating);
        return meService.getMeVendorProductReviewPage(operation).process(getMeVendorProductReviewPageProcessor);
    }
}