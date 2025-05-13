package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.dto.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.query.me.GetMeByJwtQuery;
import com.surofu.madeinrussia.application.query.me.GetMeSessionsQuery;
import com.surofu.madeinrussia.application.security.SecurityUser;
import com.surofu.madeinrussia.application.utils.IpAddressUtils;
import com.surofu.madeinrussia.core.service.me.MeService;
import com.surofu.madeinrussia.core.service.me.operation.GetMeByJwt;
import com.surofu.madeinrussia.core.service.me.operation.GetMeSessions;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/me")
@Tag(
        name = "User Profile",
        description = "API for accessing current user information"
)
@SecurityRequirement(name = "Bearer Authentication")
public class MeRestController {
    private final MeService meService;

    private final GetMeByJwt.Result.Processor<ResponseEntity<?>> getMeByJwtProcessor;
    private final GetMeSessions.Result.Processor<ResponseEntity<?>> getMeSessionsProcessor;

    private final IpAddressUtils ipAddressUtils;

    @GetMapping
    @Operation(
            summary = "Get current user information",
            description = """
                    Returns complete profile information for the currently authenticated user.
                    Requires valid JWT token with USER role.
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
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getMeByJwt(
            @AuthenticationPrincipal SecurityUser securityUser,
            HttpServletRequest request
    ) {
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = ipAddressUtils.getClientIpAddressFromHttpRequest(request);

        GetMeByJwtQuery query = new GetMeByJwtQuery(securityUser, userAgent, ipAddress);
        GetMeByJwt operation = GetMeByJwt.of(query);
        return meService.getMeByJwt(operation).process(getMeByJwtProcessor);
    }

    @GetMapping("sessions")
    @PreAuthorize("hasAnyRole('USER')")
    public ResponseEntity<?> getMeSessions(
            @AuthenticationPrincipal SecurityUser securityUser,
            HttpServletRequest request
    ) {
        String userAgent = request.getHeader("User-Agent");
        String ipAddress = ipAddressUtils.getClientIpAddressFromHttpRequest(request);

        GetMeSessionsQuery query = new GetMeSessionsQuery(securityUser, userAgent, ipAddress);
        GetMeSessions operation = GetMeSessions.of(query);
        return meService.getMeSessions(operation).process(getMeSessionsProcessor);
    }
}
