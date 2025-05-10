package com.surofu.madeinrussia.core.service.user;

import com.surofu.madeinrussia.core.service.user.operation.GetUserByEmail;
import com.surofu.madeinrussia.core.service.user.operation.GetUserById;
import com.surofu.madeinrussia.core.service.user.operation.GetUserByLogin;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    GetUserById.Result getUserById(GetUserById operation);

    GetUserByLogin.Result getUserByLogin(GetUserByLogin operation);

    GetUserByEmail.Result getUserByEmail(GetUserByEmail operation);
}
