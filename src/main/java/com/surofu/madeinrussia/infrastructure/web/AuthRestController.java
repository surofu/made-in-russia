package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.command.auth.*;
import com.surofu.madeinrussia.application.dto.auth.LoginSuccessDto;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.dto.error.ValidationExceptionDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import com.surofu.madeinrussia.core.model.user.UserRegion;
import com.surofu.madeinrussia.core.model.user.password.UserPasswordPassword;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorCountry.VendorCountryName;
import com.surofu.madeinrussia.core.model.vendorDetails.VendorDetailsInn;
import com.surofu.madeinrussia.core.model.vendorDetails.vendorProductCategory.VendorProductCategoryName;
import com.surofu.madeinrussia.core.service.auth.AuthService;
import com.surofu.madeinrussia.core.service.auth.operation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/auth")
@Tag(
        name = "Authentication",
        description = "API for user registration and authentication"
)
public class AuthRestController {
    private final AuthService authService;

    private final Register.Result.Processor<ResponseEntity<?>> registerProcessor;
    private final RegisterVendor.Result.Processor<ResponseEntity<?>> registerVendorProcessor;
    private final LoginWithEmail.Result.Processor<ResponseEntity<?>> loginWithEmailProcessor;
    private final LoginWithLogin.Result.Processor<ResponseEntity<?>> loginWithLoginProcessor;
    private final VerifyEmail.Result.Processor<ResponseEntity<?>> verifyEmailProcessor;
    private final Logout.Result.Processor<ResponseEntity<?>> logoutProcessor;
    private final RecoverPassword.Result.Processor<ResponseEntity<?>> recoverPasswordProcessor;
    private final VerifyRecoverPassword.Result.Processor<ResponseEntity<?>> verifyRecoverPasswordProcessor;

    @PostMapping("register")
    @Operation(
            summary = "Register new user",
            description = "Creates a new user account with provided credentials",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid registration data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ValidationExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Email, login or phone number already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User registration data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterCommand.class)
                    )
            )
            @RequestBody RegisterCommand registerCommand
    ) {
        Register operation = Register.of(
                UserEmail.of(registerCommand.email()),
                UserLogin.of(registerCommand.login()),
                UserPasswordPassword.of(registerCommand.password()),
                UserRegion.of(registerCommand.region()),
                UserPhoneNumber.of(registerCommand.phoneNumber())
        );
        return authService.register(operation).process(registerProcessor);
    }

    @PostMapping("register-vendor")
    @Operation(
            summary = "Register new vendor",
            description = "Creates a new vendor account with provided credentials",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Vendor created successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid registration data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ValidationExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Email, login or phone number already exists",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> registerVendor(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Vendor registration data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = RegisterVendorCommand.class)
                    )
            )
            @RequestBody @Valid RegisterVendorCommand registerVendorCommand) {
        RegisterVendor operation = RegisterVendor.of(
                UserEmail.of(registerVendorCommand.email()),
                UserLogin.of(registerVendorCommand.login()),
                UserPasswordPassword.of(registerVendorCommand.password()),
                UserRegion.of(registerVendorCommand.countries().get(0)),
                UserPhoneNumber.of(registerVendorCommand.phoneNumber()),
                VendorDetailsInn.of(registerVendorCommand.inn()),
                registerVendorCommand.countries().stream().map(VendorCountryName::of).toList(),
                registerVendorCommand.productCategories().stream().map(VendorProductCategoryName::of).toList()
        );

        return authService.registerVendor(operation).process(registerVendorProcessor);
    }

    @PostMapping("login-with-email")
    @Operation(
            summary = "Login with email",
            description = "Authenticate user using email address and password",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authentication successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginSuccessDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
            }
    )
    public ResponseEntity<?> loginWithEmail(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email login credentials",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginWithEmailCommand.class)
                    )
            )
            @RequestBody @Valid LoginWithEmailCommand command
    ) {
        LoginWithEmail operation = LoginWithEmail.of(
                UserEmail.of(command.email()),
                UserPasswordPassword.of(command.password())
        );
        return authService.loginWithEmail(operation).process(loginWithEmailProcessor);
    }

    @PostMapping("login-with-login")
    @Operation(
            summary = "Login with username",
            description = "Authenticate user using username and password",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Authentication successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = LoginSuccessDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> loginWithLogin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User login credentials",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(
                                    implementation = LoginWithLoginCommand.class,
                                    example = """
                                            {
                                                "login": "user123",
                                                "password": "password123"
                                            }
                                            """)
                    )
            )
            @RequestBody LoginWithLoginCommand command
    ) {
        LoginWithLogin operation = LoginWithLogin.of(
                UserLogin.of(command.login()),
                UserPasswordPassword.of(command.password())
        );
        return authService.loginWithLogin(operation).process(loginWithLoginProcessor);
    }

    @PostMapping("verify-email")
    @Operation(
            summary = "Verify email address",
            description = "Validates user's email using verification code",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Email verified successfully",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or expired verification code",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ValidationExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> verifyEmail(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Email verification data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = VerifyEmailCommand.class)
                    )
            )
            @RequestBody @Valid VerifyEmailCommand verifyEmailCommand,
            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        SessionInfo sessionInfo = SessionInfo.of(request);
        VerifyEmail operation = VerifyEmail.of(
                UserEmail.of(verifyEmailCommand.email()),
                verifyEmailCommand.code(),
                sessionInfo
        );
        return authService.verifyEmail(operation).process(verifyEmailProcessor);
    }

    @PreAuthorize("isAuthenticated()")
    @PostMapping("logout")
    @SecurityRequirement(name = "Bearer Authentication")
    @Operation(
            summary = "Logout user",
            description = "Terminates the current user session",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Logout successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
            }
    )
    public ResponseEntity<?> logout(@Parameter(hidden = true) @AuthenticationPrincipal SecurityUser securityUser) {
        Logout operation = Logout.of(securityUser);
        return authService.logout(operation).process(logoutProcessor);
    }

    @PostMapping("recover-password")
    @Operation(
            summary = "Recover user password",
            description = "Send recover code to email",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Code has been sent to email successful",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Bad request",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = ValidationExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
            }
    )
    public ResponseEntity<?> recoverPassword(
            @RequestBody @Valid RecoverPasswordCommand recoverPassword
    ) {
        RecoverPassword operation = RecoverPassword.of(
                UserEmail.of(recoverPassword.email()),
               UserPasswordPassword.of(recoverPassword.newPassword())
        );
        return authService.recoverPassword(operation).process(recoverPasswordProcessor);
    }

    @PostMapping("verify-recover-password")
    public ResponseEntity<?> verifyRecoverPassword(
            @RequestBody @Valid VerifyRecoverPasswordCommand verifyRecoverPasswordCommand,
            @Parameter(hidden = true)
            HttpServletRequest request
    ) {
        SessionInfo sessionInfo = SessionInfo.of(request);

        VerifyRecoverPassword operation = VerifyRecoverPassword.of(
                UserEmail.of(verifyRecoverPasswordCommand.email()),
                verifyRecoverPasswordCommand.recoverCode(),
                sessionInfo
        );
        return authService.verifyRecoverPassword(operation).process(verifyRecoverPasswordProcessor);
    }
}