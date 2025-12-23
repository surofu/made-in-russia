package com.surofu.exporteru.infrastructure.web;

import com.surofu.exporteru.application.command.user.ChangeUserRoleCommand;
import com.surofu.exporteru.application.command.user.ForceUpdateUserCommand;
import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.core.model.user.UserPhoneNumber;
import com.surofu.exporteru.core.model.user.UserRegion;
import com.surofu.exporteru.core.model.user.UserRole;
import com.surofu.exporteru.core.service.user.UserService;
import com.surofu.exporteru.core.service.user.operation.BanUserById;
import com.surofu.exporteru.core.service.user.operation.ChangeUserRoleById;
import com.surofu.exporteru.core.service.user.operation.DeleteUserAvatarById;
import com.surofu.exporteru.core.service.user.operation.DeleteUserByEmail;
import com.surofu.exporteru.core.service.user.operation.DeleteUserById;
import com.surofu.exporteru.core.service.user.operation.DeleteUserByLogin;
import com.surofu.exporteru.core.service.user.operation.ForceUpdateUserById;
import com.surofu.exporteru.core.service.user.operation.GetUserByEmail;
import com.surofu.exporteru.core.service.user.operation.GetUserById;
import com.surofu.exporteru.core.service.user.operation.GetUserByLogin;
import com.surofu.exporteru.core.service.user.operation.GetUserPage;
import com.surofu.exporteru.core.service.user.operation.SaveUserAvatarById;
import com.surofu.exporteru.core.service.user.operation.UnbanUserById;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.HashMap;
import java.util.Locale;
import lombok.RequiredArgsConstructor;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/user")
@Tag(name = "User Profile")
public class UserRestController {
  private final UserService service;
  private final GetUserPage.Result.Processor<ResponseEntity<?>> getUserPageProcessor;
  private final GetUserById.Result.Processor<ResponseEntity<?>> getUserByIdProcessor;
  private final GetUserByEmail.Result.Processor<ResponseEntity<?>> getUserByEmailProcessor;
  private final GetUserByLogin.Result.Processor<ResponseEntity<?>> getUserByLoginProcessor;
  private final ForceUpdateUserById.Result.Processor<ResponseEntity<?>>
      forceUpdateUserByIdProcessor;
  private final DeleteUserById.Result.Processor<ResponseEntity<?>> deleteUserByIdProcessor;
  private final DeleteUserByEmail.Result.Processor<ResponseEntity<?>> deleteUserByEmailProcessor;
  private final DeleteUserByLogin.Result.Processor<ResponseEntity<?>> deleteUserByLoginProcessor;
  private final BanUserById.Result.Processor<ResponseEntity<?>> banUserByIdProcessor;
  private final UnbanUserById.Result.Processor<ResponseEntity<?>> unbanUserByIdProcessor;
  private final ChangeUserRoleById.Result.Processor<ResponseEntity<?>> changeUserRoleByIdProcessor;
  private final SaveUserAvatarById.Result.Processor<ResponseEntity<?>> saveUserAvatarByIdProcessor;
  private final DeleteUserAvatarById.Result.Processor<ResponseEntity<?>>
      deleteUserAvatarByIdProcessor;

  @GetMapping
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @Operation(summary = "Get paginated list of users (Admin only)")
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
  @Operation(summary = "Get user by identifier (Admin only)")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> getUserByIdentifier(@PathVariable("identifier") String identifier) {
    Locale locale = LocaleContextHolder.getLocale();

    try {
      Long id = Long.parseLong(identifier);
      GetUserById operation = GetUserById.of(id, locale);
      return service.getUserById(operation).process(getUserByIdProcessor);
    } catch (NumberFormatException ignored) {
    }

    try {
      UserEmail email = new UserEmail(identifier);
      GetUserByEmail operation = GetUserByEmail.of(email, locale);
      return service.getUserByEmail(operation).process(getUserByEmailProcessor);
    } catch (Exception ignored) {
    }
    UserLogin login = new UserLogin(identifier, new HashMap<>());
    GetUserByLogin operation = GetUserByLogin.of(login, locale);
    return service.getUserByLogin(operation).process(getUserByLoginProcessor);
  }

  @PutMapping("{id}")
  @Operation(summary = "Update user by ID (Admin only)")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> updateUserById(
      @PathVariable("id") Long id,
      @RequestBody ForceUpdateUserCommand command) {
    ForceUpdateUserById operation = ForceUpdateUserById.of(
        id,
        new UserEmail(command.email()),
        new UserLogin(command.login(), new HashMap<>()),
        new UserPhoneNumber(command.phoneNumber()),
        new UserRegion(command.region())
    );
    return service.forceUpdateUserById(operation).process(forceUpdateUserByIdProcessor);
  }

  @DeleteMapping("{identifier}")
  @Operation(summary = "Delete user by identifier (Admin only)")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> deleteUserById(@PathVariable("identifier") String identifier) {
    Locale locale = LocaleContextHolder.getLocale();

    try {
      Long id = Long.parseLong(identifier);
      DeleteUserById operation = DeleteUserById.of(id, locale);
      return service.deleteUserById(operation).process(deleteUserByIdProcessor);
    } catch (NumberFormatException ignored) {
    }

    try {
      UserEmail email = new UserEmail(identifier);
      DeleteUserByEmail operation = DeleteUserByEmail.of(email, locale);
      return service.deleteUserByEmail(operation).process(deleteUserByEmailProcessor);
    } catch (Exception ignored) {
    }

    UserLogin login = new UserLogin(identifier, new HashMap<>());
    DeleteUserByLogin operation = DeleteUserByLogin.of(login, locale);
    return service.deleteUserByLogin(operation).process(deleteUserByLoginProcessor);
  }

  @PatchMapping("{id}/ban")
  @Operation(summary = "Ban user by ID (Admin only)")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> banUserById(@PathVariable("id") Long id) {
    BanUserById operation = BanUserById.of(id);
    return service.banUserById(operation).process(banUserByIdProcessor);
  }

  @PatchMapping("{id}/unban")
  @Operation(summary = "Unban user by ID (Admin only)")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> unbanUserById(@PathVariable("id") Long id) {
    UnbanUserById operation = UnbanUserById.of(id);
    return service.unbanUserById(operation).process(unbanUserByIdProcessor);
  }

  @PatchMapping("{id}/role")
  @Operation(summary = "Change user role by ID (Admin only)")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> changeUserRoleById(
      @PathVariable("id") Long id,
      @RequestBody ChangeUserRoleCommand command
  ) {
    ChangeUserRoleById operation = ChangeUserRoleById.of(id, UserRole.of(command.role()));
    return service.changeUserRoleById(operation).process(changeUserRoleByIdProcessor);
  }

  @PutMapping(value = "{id}/avatar", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
  @Operation(summary = "Upload user avatar by ID (Admin only)")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> saveUserAvatarById(
      @PathVariable(name = "id") Long id,
      @RequestPart("file") MultipartFile file
  ) {
    SaveUserAvatarById operation = SaveUserAvatarById.of(id, file);
    return service.saveUserAvatarById(operation).process(saveUserAvatarByIdProcessor);
  }

  @DeleteMapping("{id}/avatar")
  @Operation(summary = "Delete user avatar by ID (Admin only)")
  @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
  @SecurityRequirement(name = "Bearer Authentication")
  public ResponseEntity<?> deleteUserAvatarById(@PathVariable(name = "id") Long id) {
    DeleteUserAvatarById operation = DeleteUserAvatarById.of(id);
    return service.deleteUserAvatarById(operation).process(deleteUserAvatarByIdProcessor);
  }
}
