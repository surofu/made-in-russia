package com.surofu.madeinrussia.application.service;

import com.surofu.madeinrussia.application.dto.UserDto;
import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.repository.UserRepository;
import com.surofu.madeinrussia.core.service.me.MeService;
import com.surofu.madeinrussia.core.service.me.operation.GetMeByJwt;
import com.surofu.madeinrussia.core.service.me.operation.UpdateMeAccessToken;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.security.Principal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class MeApplicationService implements MeService {
    private final UserRepository userRepository;

    @Override
    public GetMeByJwt.Result getMeByJwt(GetMeByJwt operation) {
        Principal principal = operation.getQuery().principal();
        Optional<UserDto> userDto = getUserDtoFromPrincipal(principal);

        if (userDto.isEmpty()) {
            String rawEmail = operation.getQuery().principal().getName();
            UserEmail userEmail = UserEmail.of(rawEmail);
            return GetMeByJwt.Result.notFound(userEmail);
        }

        return GetMeByJwt.Result.success(userDto.get());
    }

    @Override
    public UpdateMeAccessToken.Result updateMeAccessToken(UpdateMeAccessToken operation) {
        return null;
    }

    private Optional<UserDto> getUserDtoFromPrincipal(Principal principal) {
        String rawEmail = principal.getName();
        UserEmail userEmail = UserEmail.of(rawEmail);
        Optional<User> user = userRepository.getUserByEmail(userEmail);
        return user.map(UserDto::of);
    }
}
