package com.surofu.madeinrussia.infrastructure.web;

import com.surofu.madeinrussia.application.command.user.ChangeUserRoleCommand;
import com.surofu.madeinrussia.application.command.user.ForceUpdateUserCommand;
import com.surofu.madeinrussia.core.model.user.*;
import com.surofu.madeinrussia.core.service.user.UserService;
import com.surofu.madeinrussia.core.service.user.operation.*;
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> getUserByIdentifier(@PathVariable("identifier") String identifier) {
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> updateUserById(@PathVariable("id") Long id, @RequestBody ForceUpdateUserCommand command) {
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteUserById(@PathVariable("identifier") String identifier) {
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
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> banUserById(@PathVariable("id") Long id) {
        BanUserById operation = BanUserById.of(id);
        return service.banUserById(operation).process(banUserByIdProcessor);
    }

    @PatchMapping("{id}/unban")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> unbanUserById(@PathVariable("id") Long id) {
        UnbanUserById operation = UnbanUserById.of(id);
        return service.unbanUserById(operation).process(unbanUserByIdProcessor);
    }

    @PatchMapping("{id}/role")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public ResponseEntity<?> changeUserRoleById(@PathVariable("id") Long id, @RequestBody ChangeUserRoleCommand command) {
        ChangeUserRoleById operation = ChangeUserRoleById.of(id, UserRole.of(command.role()));
        return service.changeUserRoleById(operation).process(changeUserRoleByIdProcessor);
    }
}
