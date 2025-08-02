package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserIsEnabled;
import com.surofu.madeinrussia.core.model.user.password.UserPassword;
import com.surofu.madeinrussia.core.repository.UserPasswordRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.user.UserService;
import com.surofu.madeinrussia.core.service.user.operation.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserApplicationService implements UserService {
    private final UserRepository userRepository;
    private final UserPasswordRepository userPasswordRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.getUserByEmail(UserEmail.of(username))
                .orElseThrow(() -> new UsernameNotFoundException(username));

        UserPassword userPassword = userPasswordRepository.getUserPasswordByUserId(user.getId())
                .orElseThrow(() -> new UsernameNotFoundException(username));

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

        if (servletRequestAttributes == null) {
            log.error("Servlet RequestAttributes is null");
            throw new UsernameNotFoundException(username);
        }

        HttpServletRequest request = servletRequestAttributes.getRequest();
        SessionInfo sessionInfo = SessionInfo.of(request);

        return new SecurityUser(user, userPassword, sessionInfo);
    }

    @Override
    @Transactional(readOnly = true)
    public GetUserById.Result getUserById(GetUserById operation) {
        Optional<User> user = userRepository.getUserById(operation.getUserId());
        Optional<UserDto> userDto = user.map(UserDto::of);

        if (userDto.isPresent()) {
            return GetUserById.Result.success(userDto.get());
        }

        return GetUserById.Result.notFound(operation.getUserId());
    }

    @Override
    @Transactional(readOnly = true)
    public GetUserByLogin.Result getUserByLogin(GetUserByLogin operation) {
        Optional<User> user = userRepository.getUserByLogin(operation.getUserLogin());
        Optional<UserDto> userDto = user.map(UserDto::of);

        if (userDto.isPresent()) {
            return GetUserByLogin.Result.success(userDto.get());
        }

        return GetUserByLogin.Result.notFound(operation.getUserLogin());
    }

    @Override
    @Transactional(readOnly = true)
    public GetUserByEmail.Result getUserByEmail(GetUserByEmail operation) {
        Optional<User> user = userRepository.getUserByEmail(operation.getUserEmail());
        Optional<UserDto> userDto = user.map(UserDto::of);

        if (userDto.isPresent()) {
            return GetUserByEmail.Result.success(userDto.get());
        }

        return GetUserByEmail.Result.notFound(operation.getUserEmail());
    }

    @Override
    @Transactional
    public ForceUpdateUserById.Result forceUpdateUserById(ForceUpdateUserById operation) {
        Optional<User> user = userRepository.getUserById(operation.getId());

        if (user.isEmpty()) {
            return ForceUpdateUserById.Result.notFound(operation.getId());
        }

        if (!user.get().getEmail().equals(operation.getEmail()) && userRepository.existsUserByEmail(operation.getEmail())) {
            return ForceUpdateUserById.Result.emailAlreadyExists(operation.getEmail());
        }

        if (!user.get().getLogin().equals(operation.getLogin()) && userRepository.existsUserByLogin(operation.getLogin())) {
            return ForceUpdateUserById.Result.loginAlreadyExists(operation.getLogin());
        }

        if (!user.get().getPhoneNumber().equals(operation.getPhoneNumber()) && userRepository.existsUserByPhoneNumber(operation.getPhoneNumber())) {
            return ForceUpdateUserById.Result.phoneNumberAlreadyExists(operation.getPhoneNumber());
        }

        user.get().setEmail(operation.getEmail());
        user.get().setLogin(operation.getLogin());
        user.get().setPhoneNumber(operation.getPhoneNumber());
        user.get().setRegion(operation.getRegion());

        try {
            userRepository.saveUser(user.get());
            return ForceUpdateUserById.Result.success(operation.getId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return ForceUpdateUserById.Result.saveError(operation.getId(), e);
        }
    }

    @Override
    @Transactional
    public DeleteUserById.Result deleteUserById(DeleteUserById operation) {
        Optional<User> user = userRepository.getUserById(operation.getId());

        if (user.isEmpty()) {
            return DeleteUserById.Result.notFound(operation.getId());
        }

        try {
            userRepository.delete(user.get());
            return DeleteUserById.Result.success(operation.getId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DeleteUserById.Result.deleteError(operation.getId(), e);
        }
    }

    @Override
    @Transactional
    public DeleteUserByEmail.Result deleteUserByEmail(DeleteUserByEmail operation) {
        Optional<User> user = userRepository.getUserByEmail(operation.getEmail());

        if (user.isEmpty()) {
            return DeleteUserByEmail.Result.notFound(operation.getEmail());
        }

        try {
            userRepository.delete(user.get());
            return DeleteUserByEmail.Result.success(operation.getEmail());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DeleteUserByEmail.Result.deleteError(operation.getEmail(), e);
        }
    }

    @Override
    @Transactional
    public DeleteUserByLogin.Result deleteUserByLogin(DeleteUserByLogin operation) {
        Optional<User> user = userRepository.getUserByLogin(operation.getLogin());

        if (user.isEmpty()) {
            return DeleteUserByLogin.Result.notFound(operation.getLogin());
        }

        try {
            userRepository.delete(user.get());
            return DeleteUserByLogin.Result.success(operation.getLogin());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return DeleteUserByLogin.Result.deleteError(operation.getLogin(), e);
        }
    }

    @Override
    @Transactional
    public BanUserById.Result banUserById(BanUserById operation) {
        Optional<User> user = userRepository.getUserById(operation.getId());

        if (user.isEmpty()) {
            return BanUserById.Result.notFound(operation.getId());
        }

        user.get().setIsEnabled(UserIsEnabled.of(false));

        try {
            userRepository.saveUser(user.get());
            return BanUserById.Result.success(operation.getId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return BanUserById.Result.saveError(operation.getId(), e);
        }
    }

    @Override
    @Transactional
    public UnbanUserById.Result unbanUserById(UnbanUserById operation) {
        Optional<User> user = userRepository.getUserById(operation.getId());

        if (user.isEmpty()) {
            return UnbanUserById.Result.notFound(operation.getId());
        }

        user.get().setIsEnabled(UserIsEnabled.of(true));

        try {
            userRepository.saveUser(user.get());
            return UnbanUserById.Result.success(operation.getId());
        } catch (Exception e) {
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return UnbanUserById.Result.saveError(operation.getId(), e);
        }
    }
}
