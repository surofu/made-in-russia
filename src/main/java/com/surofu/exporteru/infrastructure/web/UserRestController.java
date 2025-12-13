package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.command.user.ChangeUserRoleCommand;
import com.surofu.exporteru.application.command.user.ForceUpdateUserCommand;
import com.surofu.exporteru.application.dto.SimpleResponseMessageDto;
import com.surofu.exporteru.application.dto.user.UserDto;
import com.surofu.exporteru.application.dto.category.UserPageDto;
import com.surofu.exporteru.application.dto.error.SimpleResponseErrorDto;
import com.surofu.exporteru.core.model.user.*;
import com.surofu.exporteru.core.service.user.UserService;
import com.surofu.exporteru.core.service.user.operation.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Locale;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(
        name = "User Profile",
        description = "API for accessing user information"
)
public class UserRestController {

    private final UserService service;

    private final GetUserPage.Result.Processor<ResponseEntity<?>> getUserPageProcessor;
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
    private final SaveUserAvatarById.Result.Processor<ResponseEntity<?>> saveUserAvatarByIdProcessor;
    private final DeleteUserAvatarById.Result.Processor<ResponseEntity<?>> deleteUserAvatarByIdProcessor;

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @Operation(
            summary = "Get paginated list of users (Admin only)",
            description = "Retrieves a paginated and filtered list of users. Requires ADMIN role.",
            security = @SecurityRequirement(name = "Bearer Authentication"),
            parameters = {
                    @Parameter(
                            name = "page",
                            description = "Zero-based page index (0..N)",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "integer", defaultValue = "0", minimum = "0")
                    ),
                    @Parameter(
                            name = "size",
                            description = "Number of items per page",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "integer", defaultValue = "10", minimum = "1", maximum = "100")
                    ),
                    @Parameter(
                            name = "role",
                            description = "Filter by user role",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", example = "ROLE_USER")
                    ),
                    @Parameter(
                            name = "isEnabled",
                            description = "Filter by account enabled status",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "boolean", example = "true")
                    ),
                    @Parameter(
                            name = "login",
                            description = "Filter by user login (partial match)",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", example = "john")
                    ),
                    @Parameter(
                            name = "email",
                            description = "Filter by email (partial match)",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", example = "example.com")
                    ),
                    @Parameter(
                            name = "phoneNumber",
                            description = "Filter by phone number (partial match)",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", example = "+7900")
                    ),
                    @Parameter(
                            name = "region",
                            description = "Filter by region",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", example = "Europe")
                    ),
                    @Parameter(
                            name = "sort",
                            description = "Sorting field",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", example = "createdAt")
                    ),
                    @Parameter(
                            name = "direction",
                            description = "Sort direction (asc/desc)",
                            in = ParameterIn.QUERY,
                            schema = @Schema(type = "string", defaultValue = "asc", allowableValues = {"asc", "desc"})
                    )
            },
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully retrieved user list",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = UserPageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "400",
                            description = "Invalid request parameters",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "401",
                            description = "Unauthorized - authentication required",
                            content = @Content
                    ),
                    @ApiResponse(
                            responseCode = "403",
                            description = "Forbidden - insufficient permissions",
                            content = @Content
                    )
            }
    )
    public ResponseEntity<?> getUserPage(
            @RequestParam(name = "page", defaultValue = "0", required = false) Integer page,
            @RequestParam(name = "size", defaultValue = "10", required = false) Integer size,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "isEnabled", required = false) Boolean isEnabled,
            @RequestParam(name = "login", required = false) String login,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(name = "phoneNumber", required = false) String phoneNumber,
            @RequestParam(name = "region", required = false) String region,
            @RequestParam(name = "sort", required = false, defaultValue = "id") String sort,
            @RequestParam(name = "direction", defaultValue = "asc", required = false) String direction
    ) {
        Locale locale = LocaleContextHolder.getLocale();
        GetUserPage operation = GetUserPage.of(page, size, role, isEnabled,
                login, email, phoneNumber, region, sort, direction, locale);
        return service.getUserPage(operation).process(getUserPageProcessor);
    }


    @GetMapping("{identifier}")
    @Operation(
            summary = "Get user by identifier (Admin only)",
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
        Locale locale = LocaleContextHolder.getLocale();
        try {
            Long id = Long.parseLong(identifier);
            GetUserById operation = GetUserById.of(id, locale);
            return service.getUserById(operation).process(getUserByIdProcessor);
        } catch (NumberFormatException ignored) {
        }

        try {
            UserEmail email = UserEmail.of(identifier);
            GetUserByEmail operation = GetUserByEmail.of(email, locale);
            return service.getUserByEmail(operation).process(getUserByEmailProcessor);
        } catch (Exception ignored) {
        }
        UserLogin login = new UserLogin(identifier);
        GetUserByLogin operation = GetUserByLogin.of(login, locale);
        return service.getUserByLogin(operation).process(getUserByLoginProcessor);
    }

    @PutMapping("{id}")
    @Operation(
            summary = "Update user by ID (Admin only)",
            description = "Updates user information by ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully updated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
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
                new UserLogin(command.login()),
                UserPhoneNumber.of(command.phoneNumber()),
                UserRegion.of(command.region())
        );
        return service.forceUpdateUserById(operation).process(forceUpdateUserByIdProcessor);
    }

    @DeleteMapping("{identifier}")
    @Operation(
            summary = "Delete user by identifier (Admin only)",
            description = "Deletes a user by ID, email or login",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully deleted",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
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
    public ResponseEntity<?> deleteUserById(
            @Parameter(
                    name = "identifier",
                    description = "User identifier (ID, email or login) to delete",
                    required = true,
                    example = "123 or 'user@example.com' or 'username'"
            )
            @PathVariable("identifier") String identifier) {
        Locale locale = LocaleContextHolder.getLocale();

        try {
            Long id = Long.parseLong(identifier);
            DeleteUserById operation = DeleteUserById.of(id, locale);
            return service.deleteUserById(operation).process(deleteUserByIdProcessor);
        } catch (NumberFormatException ignored) {
        }

        try {
            UserEmail email = UserEmail.of(identifier);
            DeleteUserByEmail operation = DeleteUserByEmail.of(email, locale);
            return service.deleteUserByEmail(operation).process(deleteUserByEmailProcessor);
        } catch (Exception ignored) {
        }

        UserLogin login = new UserLogin(identifier);
        DeleteUserByLogin operation = DeleteUserByLogin.of(login, locale);
        return service.deleteUserByLogin(operation).process(deleteUserByLoginProcessor);
    }

    @PatchMapping("{id}/ban")
    @Operation(
            summary = "Ban user by ID (Admin only)",
            description = "Bans a user by their ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully banned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
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
            summary = "Unban user by ID (Admin only)",
            description = "Unbans a user by their ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User successfully unbanned",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
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
            summary = "Change user role by ID (Admin only)",
            description = "Changes the role of a user by their ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "User role successfully changed",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
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

    @PutMapping(value = "{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
            summary = "Upload user avatar by ID (Admin only)",
            description = "Uploads or replaces the avatar for a user by their ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Avatar successfully uploaded",
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
    public ResponseEntity<?> saveUserAvatarById(
            @Parameter(
                    name = "id",
                    description = "ID of the user whose avatar will be updated",
                    required = true,
                    example = "123",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable(name = "id") Long id,
            @Parameter(
                    name = "file",
                    description = "Image file to upload as avatar (JPG, PNG, etc.)",
                    required = true
            )
            @RequestPart("file") MultipartFile file) {
        SaveUserAvatarById operation = SaveUserAvatarById.of(id, file);
        return service.saveUserAvatarById(operation).process(saveUserAvatarByIdProcessor);
    }

    @DeleteMapping("{id}/avatar")
    @Operation(
            summary = "Delete user avatar by ID (Admin only)",
            description = "Deletes the avatar for a user by their ID",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Avatar successfully deleted",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseMessageDto.class)
                            )
                    ),
                    @ApiResponse(
                            responseCode = "404",
                            description = "User not found or has no avatar",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = SimpleResponseErrorDto.class)
                            )
                    )
            }
    )
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    @SecurityRequirement(name = "Bearer Authentication")
    public ResponseEntity<?> deleteUserAvatarById(
            @Parameter(
                    name = "id",
                    description = "ID of the user whose avatar will be deleted",
                    required = true,
                    example = "123",
                    schema = @Schema(type = "integer", format = "int64", minimum = "1")
            )
            @PathVariable(name = "id") Long id) {
        DeleteUserAvatarById operation = DeleteUserAvatarById.of(id);
        return service.deleteUserAvatarById(operation).process(deleteUserAvatarByIdProcessor);
    }
}
