package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.command.user.ChangeUserRoleCommand;
import com.surofu.madeinrussia.application.command.user.ForceUpdateUserCommand;
import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.dto.error.SimpleResponseErrorDto;
import com.surofu.madeinrussia.core.model.user.*;
import com.surofu.madeinrussia.core.service.user.UserService;
import com.surofu.madeinrussia.core.service.user.operation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(
        name = "User Profile",
        description = "API for accessing user information"
)
public class UserRestController {

    private final UserService service;

    private final GetUserById.Result.Processor<ResponseEntity<?>> getUserByIdProcessor;
    private final GetUserByEmail.Result.Processor<ResponseEntity<?>> getUserByEmailProcessor;
    private final GetUserByLogin.Result.Processor<ResponseEntity<?>> getUserByLoginProcessor;
    private final ForceUpdateUserById.Result.Processor<ResponseEntity<?>> forceUpdateUserByIdProcessor;
    private final DeleteUserById.Result.Processor<ResponseEntity<?>> deleteUserByIdProcessor;
    private final DeleteUserByEmail.Result.Processor<ResponseEntity<?>> deleteUserByEmailProcessor;
    private final DeleteUserByLogin.Result.Processor<ResponseEntity<?>> deleteUserByLoginProcessor;
    private final BanUserById.Result.Processor<ResponseEntity<?>> banUserByIdProcessor;
    private final UnbanUserById.Result.Processor<ResponseEntity<?>> unbanUserByIdProcessor;
    private final ChangeUserRoleById.Result.Processor<ResponseEntity<?>> changeUserRoleByIdProcessor;

    @GetMapping("{identifier}")
    @Operation(
            summary = "Get user by identifier",
            description = "Retrieves a user by ID, email or login",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User found and returned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> getUserByIdentifier(
            @Parameter(
                    name = "identifier",
                    description = "User identifier (ID, email or login)",
                    required = true,
                    example = "123 or 'user@example.com' or 'username'"
            )
            @PathVariable("identifier") String identifier) {
        try {
            Long id = Long.parseLong(identifier);
            GetUserById operation = GetUserById.of(id);
            return service.getUserById(operation).process(getUserByIdProcessor);
        } catch (NumberFormatException ignored) {
        }

        try {
            UserEmail email = UserEmail.of(identifier);
            GetUserByEmail operation = GetUserByEmail.of(email);
            return service.getUserByEmail(operation).process(getUserByEmailProcessor);
        } catch (Exception ignored) {
        }

        UserLogin login = UserLogin.of(identifier);
        GetUserByLogin operation = GetUserByLogin.of(login);
        return service.getUserByLogin(operation).process(getUserByLoginProcessor);
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Update user by ID",
            description = "Updates user information by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid input data",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> updateUserById(
            @Parameter(
                    name = "id",
                    description = "ID of the user to be updated",
                    required = true,
                    example = "123",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable("id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "User update data",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ForceUpdateUserCommand.class)
                    )
            )
            @RequestBody ForceUpdateUserCommand command) {
        ForceUpdateUserById operation = ForceUpdateUserById.of(
                id,
                UserEmail.of(command.email()),
                UserLogin.of(command.login()),
                UserPhoneNumber.of(command.phoneNumber()),
                UserRegion.of(command.region())
        );
        return service.forceUpdateUserById(operation).process(forceUpdateUserByIdProcessor);
    }

    @DeleteMapping("{identifier}")
    @Operation(
            summary = "Delete user by identifier",
            description = "Deletes a user by ID, email or login",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully deleted"
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> deleteUserById(
            @Parameter(
                    name = "identifier",
                    description = "User identifier (ID, email or login) to delete",
                    required = true,
                    example = "123 or 'user@example.com' or 'username'"
            )
            @PathVariable("identifier") String identifier) {
        try {
            Long id = Long.parseLong(identifier);
            DeleteUserById operation = DeleteUserById.of(id);
            return service.deleteUserById(operation).process(deleteUserByIdProcessor);
        } catch (NumberFormatException ignored) {
        }

        try {
            UserEmail email = UserEmail.of(identifier);
            DeleteUserByEmail operation = DeleteUserByEmail.of(email);
            return service.deleteUserByEmail(operation).process(deleteUserByEmailProcessor);
        } catch (Exception ignored) {
        }

        UserLogin login = UserLogin.of(identifier);
        DeleteUserByLogin operation = DeleteUserByLogin.of(login);
        return service.deleteUserByLogin(operation).process(deleteUserByLoginProcessor);
    }

    @PatchMapping("{id}/ban")
    @Operation(
            summary = "Ban user by ID",
            description = "Bans a user by their ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully banned"
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> banUserById(
            @Parameter(
                    name = "id",
                    description = "ID of the user to ban",
                    required = true,
                    example = "123",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable("id") Long id) {
        BanUserById operation = BanUserById.of(id);
        return service.banUserById(operation).process(banUserByIdProcessor);
    }

    @PatchMapping("{id}/unban")
    @Operation(
            summary = "Unban user by ID",
            description = "Unbans a user by their ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully unbanned"
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> unbanUserById(
            @Parameter(
                    name = "id",
                    description = "ID of the user to unban",
                    required = true,
                    example = "123",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable("id") Long id) {
        UnbanUserById operation = UnbanUserById.of(id);
        return service.unbanUserById(operation).process(unbanUserByIdProcessor);
    }

    @PatchMapping("{id}/role")
    @Operation(
            summary = "Change user role by ID",
            description = "Changes the role of a user by their ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User role successfully changed"
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid role provided",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found"
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> changeUserRoleById(
            @Parameter(
                    name = "id",
                    description = "ID of the user whose role will be changed",
                    required = true,
                    example = "123",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable("id") Long id,
            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "New role for the user",
                    required = true,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = ChangeUserRoleCommand.class)
                    )
            )
            @RequestBody ChangeUserRoleCommand command) {
        ChangeUserRoleById operation = ChangeUserRoleById.of(id, UserRole.of(command.role()));
        return service.changeUserRoleById(operation).process(changeUserRoleByIdProcessor);
    }
}
