package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.model.SessionInfo;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
import com.surofu.madeinrussia.core.repository.UserPasswordRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.user.UserService;
import com.surofu.madeinrussia.core.service.user.operation.GetUserByEmail;
import com.surofu.madeinrussia.core.service.user.operation.GetUserById;
import com.surofu.madeinrussia.core.service.user.operation.GetUserByLogin;
import com.surofu.madeinrussia.application.model.SecurityUser;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
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
    public GetUserById.Result getUserById(GetUserById operation) {
        Optional<User> user = userRepository.getUserById(operation.getQuery().userId());
        Optional<UserDto> userDto = user.map(UserDto::of);

        if (userDto.isPresent()) {
            return GetUserById.Result.success(userDto.get());
        }

        return GetUserById.Result.notFound(operation.getQuery().userId());
    }

    @Override
    public GetUserByLogin.Result getUserByLogin(GetUserByLogin operation) {
        Optional<User> user = userRepository.getUserByLogin(UserLogin.of(operation.getQuery().userLogin()));
        Optional<UserDto> userDto = user.map(UserDto::of);

        if (userDto.isPresent()) {
            return GetUserByLogin.Result.success(userDto.get());
        }

        return GetUserByLogin.Result.notFound(operation.getQuery().userLogin());
    }

    @Override
    public GetUserByEmail.Result getUserByEmail(GetUserByEmail operation) {
        Optional<User> user = userRepository.getUserByEmail(UserEmail.of(operation.getQuery().userEmail()));
        Optional<UserDto> userDto = user.map(UserDto::of);

        if (userDto.isPresent()) {
            return GetUserByEmail.Result.success(userDto.get());
        }

        return GetUserByEmail.Result.notFound(operation.getQuery().userEmail());
    }

    @Override
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
}
