package com.surofu.exporteru.application.service;

import com.surofu.exporteru.application.components.TransliterationManager;
import com.surofu.exporteru.application.dto.AbstractAccountDto;
import com.surofu.exporteru.application.dto.user.UserDto;
import com.surofu.exporteru.application.dto.vendor.VendorDto;
import com.surofu.exporteru.application.enums.FileStorageFolders;
import com.surofu.exporteru.application.model.security.SecurityUser;
import com.surofu.exporteru.application.model.session.SessionInfo;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserAvatar;
import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.model.user.UserIsEnabled;
import com.surofu.exporteru.core.repository.FileStorageRepository;
import com.surofu.exporteru.core.repository.UserRepository;
import com.surofu.exporteru.core.repository.specification.UserSpecifications;
import com.surofu.exporteru.core.service.mail.MailService;
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
import jakarta.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.List;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationService implements UserService {
  private final UserRepository userRepository;
  private final FileStorageRepository fileStorageRepository;
  private final MailService mailService;

  @Override
  @Transactional(readOnly = true)
  public GetUserPage.Result getUserPage(GetUserPage operation) {
    String[] sortStrings = operation.getSort().split(",");
    Sort sort = Sort.by(Sort.Direction.fromString(operation.getDirection()), sortStrings);
    Pageable pageable = PageRequest.of(operation.getPage(), operation.getSize(), sort);
    Specification<User> specification =
        Specification.where(UserSpecifications.byRole(operation.getRole()))
            .and(UserSpecifications.byIsEnabled(operation.getIsEnabled()))
            .and(UserSpecifications.byEmail(operation.getEmail()))
            .and(UserSpecifications.byLogin(operation.getLogin()))
            .and(UserSpecifications.byPhoneNumber(operation.getPhoneNumber()))
            .and(UserSpecifications.byRegion(operation.getRegion()));
    Page<User> page = userRepository.getPage(specification, pageable);
    List<Long> ids = page.getContent().stream().map(User::getId).toList();
    List<User> fullUserList = userRepository.getByIds(ids);
    Page<AbstractAccountDto> dtoPage = page.map(user -> {
      User fullUser = fullUserList.stream()
          .filter(u -> u.getId().equals(user.getId()))
          .findFirst()
          .orElse(null);
      if (fullUser == null) {
        return UserDto.of(user);
      }
      if (fullUser.getVendorDetails() != null) {
        return VendorDto.of(fullUser);
      }
      return UserDto.of(fullUser);
    });
    return GetUserPage.Result.success(dtoPage);
  }

  @Override
  @Transactional(readOnly = true)
  public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
    User user = userRepository.getUserByEmail(new UserEmail(username))
        .orElseThrow(() -> new UsernameNotFoundException(username));
    ServletRequestAttributes servletRequestAttributes =
        (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
    if (servletRequestAttributes == null) {
      log.error("Servlet RequestAttributes is null");
      throw new UsernameNotFoundException(username);
    }
    HttpServletRequest request = servletRequestAttributes.getRequest();
    SessionInfo sessionInfo = SessionInfo.of(request);
    return new SecurityUser(user, user.getPassword(), sessionInfo);
  }

  @Override
  @Transactional(readOnly = true)
  public GetUserById.Result getUserById(GetUserById operation) {
    Optional<User> userOptional = userRepository.getById(operation.getUserId());
    if (userOptional.isEmpty()) {
      return GetUserById.Result.notFound(operation.getUserId());
    }

    User user = userOptional.get();
    if (user.getVendorDetails() != null) {
      VendorDto dto = VendorDto.of(user);
      return GetUserById.Result.success(dto);
    }

    UserDto dto = UserDto.of(user);
    return GetUserById.Result.success(dto);
  }

  @Override
  @Transactional(readOnly = true)
  public GetUserByLogin.Result getUserByLogin(GetUserByLogin operation) {
    Optional<User> userOptional = userRepository.getUserByLogin(operation.getLogin());

    if (userOptional.isEmpty()) {
      return GetUserByLogin.Result.notFound(operation.getLogin());
    }

    User user = userOptional.get();
    if (user.getVendorDetails() != null) {
      VendorDto dto = VendorDto.of(user);
      return GetUserByLogin.Result.success(dto);
    }

    UserDto dto = UserDto.of(user);
    return GetUserByLogin.Result.success(dto);
  }

  @Override
  @Transactional(readOnly = true)
  public GetUserByEmail.Result getUserByEmail(GetUserByEmail operation) {
    Optional<User> userOptional = userRepository.getUserByEmail(operation.getEmail());

    if (userOptional.isEmpty()) {
      return GetUserByEmail.Result.notFound(operation.getEmail());
    }

    User user = userOptional.get();
    if (user.getVendorDetails() != null) {
      VendorDto dto = VendorDto.of(user);
      return GetUserByEmail.Result.success(dto);
    }

    UserDto dto = UserDto.of(user);
    return GetUserByEmail.Result.success(dto);
  }

  @Override
  @Transactional
  public ForceUpdateUserById.Result forceUpdateUserById(ForceUpdateUserById operation) {
    try {
      Optional<User> userOptional = userRepository.getById(operation.getId());
      if (userOptional.isEmpty()) {
        return ForceUpdateUserById.Result.notFound(operation.getId());
      }

      User user = userOptional.get();
      if (!user.getEmail().equals(operation.getEmail()) &&
          userRepository.existsUserByEmail(operation.getEmail())) {
        return ForceUpdateUserById.Result.emailAlreadyExists(operation.getEmail());
      }
      if (!user.getLogin().equals(operation.getLogin()) &&
          userRepository.existsUserByLogin(operation.getLogin())) {
        return ForceUpdateUserById.Result.loginAlreadyExists(operation.getLogin());
      }
      if (user.getPhoneNumber() != null && !user.getPhoneNumber().equals(operation.getPhoneNumber()) &&
          userRepository.existsUserByPhoneNumber(operation.getPhoneNumber())) {
        return ForceUpdateUserById.Result.phoneNumberAlreadyExists(operation.getPhoneNumber());
      }

      user.setEmail(operation.getEmail());
      user.setLogin(
          TransliterationManager.transliterateUserLogin(operation.getLogin(),
              LocaleContextHolder.getLocale()));
      user.setPhoneNumber(operation.getPhoneNumber());
      user.setRegion(operation.getRegion());
      userRepository.save(user);
      return ForceUpdateUserById.Result.success(operation.getId());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return ForceUpdateUserById.Result.saveError(operation.getId(), e);
    }
  }

  @Override
  @Transactional
  public DeleteUserById.Result deleteUserById(DeleteUserById operation) {
    Optional<User> user = userRepository.getById(operation.getId());

    if (user.isEmpty()) {
      return DeleteUserById.Result.notFound(operation.getId());
    }

    try {
      userRepository.delete(user.get());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return DeleteUserById.Result.deleteError(operation.getId(), e);
    }

    try {
      mailService.sendDeleteAccountMail(user.get().getEmail().toString(), operation.getLocale());
    } catch (IOException e) {
      log.warn(e.getMessage(), e);
    }
    return DeleteUserById.Result.success(operation.getId());
  }

  @Override
  @Transactional
  public DeleteUserByEmail.Result deleteUserByEmail(DeleteUserByEmail operation) {
    Optional<User> userOptional = userRepository.getUserByEmail(operation.getEmail());

    if (userOptional.isEmpty()) {
      return DeleteUserByEmail.Result.notFound(operation.getEmail());
    }

    User user = userOptional.get();
    try {
      userRepository.delete(user);
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return DeleteUserByEmail.Result.deleteError(operation.getEmail(), e);
    }

    try {
      mailService.sendDeleteAccountMail(user.getEmail().toString(), operation.getLocale());
      return DeleteUserByEmail.Result.success(operation.getEmail());
    } catch (IOException e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return DeleteUserByEmail.Result.deleteError(operation.getEmail(), e);
    }
  }

  @Override
  @Transactional
  public DeleteUserByLogin.Result deleteUserByLogin(DeleteUserByLogin operation) {
    try {
      Optional<User> userOptional = userRepository.getUserByLogin(operation.getLogin());

      if (userOptional.isEmpty()) {
        return DeleteUserByLogin.Result.notFound(operation.getLogin());
      }

      User user = userOptional.get();
      userRepository.delete(user);
      mailService.sendDeleteAccountMail(user.getEmail().toString(), operation.getLocale());
      return DeleteUserByLogin.Result.success(operation.getLogin());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return DeleteUserByLogin.Result.deleteError(operation.getLogin(), e);
    }
  }

  @Override
  @Transactional
  public BanUserById.Result banUserById(BanUserById operation) {
    try {
      Optional<User> userOptional = userRepository.getById(operation.getId());

      if (userOptional.isEmpty()) {
        return BanUserById.Result.notFound(operation.getId());
      }

      User user = userOptional.get();
      user.setIsEnabled(new UserIsEnabled(false));
      userRepository.save(user);
      return BanUserById.Result.success(operation.getId());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return BanUserById.Result.saveError(operation.getId(), e);
    }
  }

  @Override
  @Transactional
  public UnbanUserById.Result unbanUserById(UnbanUserById operation) {
    try {
      Optional<User> userOptional = userRepository.getById(operation.getId());

      if (userOptional.isEmpty()) {
        return UnbanUserById.Result.notFound(operation.getId());
      }

      User user = userOptional.get();
      user.setIsEnabled(new UserIsEnabled(true));
      userRepository.save(user);
      return UnbanUserById.Result.success(operation.getId());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return UnbanUserById.Result.saveError(operation.getId(), e);
    }
  }

  @Override
  @Transactional
  public ChangeUserRoleById.Result changeUserRoleById(ChangeUserRoleById operation) {
    try {
      Optional<User> userOptional = userRepository.getById(operation.getId());

      if (userOptional.isEmpty()) {
        return ChangeUserRoleById.Result.notFound(operation.getId());
      }

      User user = userOptional.get();
      user.setRole(operation.getRole());
      userRepository.save(user);
      return ChangeUserRoleById.Result.success(operation.getId());
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return ChangeUserRoleById.Result.saveError(operation.getId(), e);
    }
  }

  @Override
  @Transactional
  public SaveUserAvatarById.Result saveUserAvatarById(SaveUserAvatarById operation) {
    Optional<User> user = userRepository.getById(operation.getId());

    if (user.isEmpty()) {
      return SaveUserAvatarById.Result.notFound(operation.getId());
    }

    if (operation.getFile() != null && !operation.getFile().isEmpty()) {
      try {
        String url = fileStorageRepository.uploadImageToFolder(operation.getFile(),
            FileStorageFolders.USERS_AVATARS.getValue());
        user.get().setAvatar(new UserAvatar(url));
      } catch (Exception e) {
        TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        return SaveUserAvatarById.Result.saveError(e);
      }
    }

    try {
      userRepository.save(user.get());
      return SaveUserAvatarById.Result.success();
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return SaveUserAvatarById.Result.saveError(e);
    }
  }

  @Override
  @Transactional
  public DeleteUserAvatarById.Result deleteUserAvatarById(DeleteUserAvatarById operation) {
    try {
      Optional<User> userOptional = userRepository.getById(operation.getId());
      if (userOptional.isEmpty()) {
        return DeleteUserAvatarById.Result.notFound(operation.getId());
      }
      User user = userOptional.get();
      user.setAvatar(null);
      userRepository.save(user);
      return DeleteUserAvatarById.Result.success();
    } catch (Exception e) {
      TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
      return DeleteUserAvatarById.Result.deleteError(e);
    }
  }
}
