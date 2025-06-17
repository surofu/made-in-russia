package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.application.model.security.SecurityUser;
import com.surofu.madeinrussia.application.model.session.SessionInfo;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
import com.surofu.madeinrussia.core.repository.UserPasswordRepository;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.user.UserService;
import com.surofu.madeinrussia.core.service.user.operation.GetUserByEmail;
import com.surofu.madeinrussia.core.service.user.operation.GetUserById;
import com.surofu.madeinrussia.core.service.user.operation.GetUserByLogin;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
//    @Cacheable(
//            value = "userById",
//            key = "#operation.userId",
//            unless = "#result instanceof T(com.surofu.madeinrussia.core.service.user.operation.GetUserById$Result$NotFound)"
//    )
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
//    @Cacheable(
//            value = "userByLogin",
//            key = "#operation.userLogin.value",
//            unless = "#result instanceof T(com.surofu.madeinrussia.core.service.user.operation.GetUserByLogin$Result$NotFound)"
//    )
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
//    @Cacheable(
//            value = "userByEmail",
//            key = "#operation.userEmail.value",
//            unless = "#result instanceof T(com.surofu.madeinrussia.core.service.user.operation.GetUserByEmail$Result$NotFound)"
//    )
    public GetUserByEmail.Result getUserByEmail(GetUserByEmail operation) {
        Optional<User> user = userRepository.getUserByEmail(operation.getUserEmail());
        Optional<UserDto> userDto = user.map(UserDto::of);

        if (userDto.isPresent()) {
            return GetUserByEmail.Result.success(userDto.get());
        }

        return GetUserByEmail.Result.notFound(operation.getUserEmail());
    }

    @Override
    @Transactional(readOnly = true)
//    @Cacheable(
//            value = "userByUsername",
//            key = "#username"
//    )
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

        System.out.println("Browser from lad: " + sessionInfo.getUserAgent().getBrowser().getId());
        System.out.println("Browser from lad: " + sessionInfo.getUserAgent().getBrowser().getBrowserType());
        System.out.println("Browser from lad: " + sessionInfo.getUserAgent().getBrowser().getGroup());
        System.out.println("Browser from lad: " + sessionInfo.getUserAgent().getBrowser().getManufacturer());

        return new SecurityUser(user, userPassword, sessionInfo);
    }
}
