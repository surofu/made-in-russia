package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.command.auth.LoginWithEmailCommand;
import com.surofu.madeinrussia.application.command.auth.LoginWithLoginCommand;
import com.surofu.madeinrussia.application.command.auth.RegisterCommand;
import com.surofu.madeinrussia.application.command.auth.VerifyEmailCommand;
import com.surofu.madeinrussia.application.dto.LoginSuccessDto;
import com.surofu.madeinrussia.application.dto.SimpleResponseErrorDto;
import com.surofu.madeinrussia.application.dto.SimpleResponseMessageDto;
import com.surofu.madeinrussia.application.dto.ValidationExceptionDto;
import com.surofu.madeinrussia.application.model.SecurityUser;
import com.surofu.madeinrussia.core.service.auth.AuthService;
import com.surofu.madeinrussia.core.service.auth.operation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    private final LoginWithEmail.Result.Processor<ResponseEntity<?>> loginWithEmailProcessor;
    private final LoginWithLogin.Result.Processor<ResponseEntity<?>> loginWithLoginProcessor;
    private final VerifyEmail.Result.Processor<ResponseEntity<?>> verifyEmailProcessor;
    private final Logout.Result.Processor<ResponseEntity<?>> logoutProcessor;

    @PostMapping("register")
    @Operation(
            summary = "Register new user",
            description = "Creates a new user account with provided credentials",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User created successfully",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid registration data",
                            content = @Content(
                                    schema = @Schema(implementation = ValidationExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "409",
                            description = "Email or login already exists",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> register(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Registration data",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = RegisterCommand.class)
                    )
            )
            @RequestBody RegisterCommand registerCommand
    ) {
        Register operation = Register.of(registerCommand);
        return authService.register(operation).process(registerProcessor);
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
                                    schema = @Schema(implementation = LoginSuccessDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials",
                            content = @Content(
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
                            schema = @Schema(implementation = LoginWithEmailCommand.class)
                    )
            )
            @RequestBody @Valid LoginWithEmailCommand loginWithEmailCommand
    ) {
        LoginWithEmail operation = LoginWithEmail.of(loginWithEmailCommand);
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
                                    schema = @Schema(implementation = LoginSuccessDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Invalid credentials",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    public ResponseEntity<?> loginWithLogin(
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Username login credentials",
                    required = true,
                    content = @Content(
                            schema = @Schema(implementation = LoginWithLoginCommand.class)
                    )
            )
            @RequestBody @Valid LoginWithLoginCommand loginWithLoginCommand
    ) {
        LoginWithLogin operation = LoginWithLogin.of(loginWithLoginCommand);
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
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid or expired verification code",
                            content = @Content(
                                    schema = @Schema(implementation = ValidationExceptionDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found",
                            content = @Content(
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
                            schema = @Schema(implementation = VerifyEmailCommand.class)
                    )
            )
            @RequestBody @Valid VerifyEmailCommand verifyEmailCommand,
            @AuthenticationPrincipal SecurityUser securityUser
    ) {
        VerifyEmail operation = VerifyEmail.of(verifyEmailCommand, securityUser.getSessionInfo());
        return authService.verifyEmail(operation).process(verifyEmailProcessor);
    }

    @PostMapping("logout")
    @Operation(
            summary = "Logout user",
            description = "Terminates the current user session",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Logout successful",
                            content = @Content(
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid session data",
                            content = @Content(
                                    schema = @Schema(implementation = ValidationExceptionDto.class)
                            )
                    ),
            }
    )
    public ResponseEntity<?> logout(@AuthenticationPrincipal SecurityUser securityUser) {
        Logout operation = Logout.of(securityUser);
        return authService.logout(operation).process(logoutProcessor);
    }
}