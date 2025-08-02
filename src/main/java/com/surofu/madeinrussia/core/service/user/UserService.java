package com.surofu.madeinrussia.core.service.user;

import com.surofu.madeinrussia.core.service.user.operation.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface UserService extends UserDetailsService {
    GetUserById.Result getUserById(GetUserById operation);

    GetUserByLogin.Result getUserByLogin(GetUserByLogin operation);

    GetUserByEmail.Result getUserByEmail(GetUserByEmail operation);

    ForceUpdateUserById.Result forceUpdateUserById(ForceUpdateUserById operation);

    DeleteUserById.Result deleteUserById(DeleteUserById operation);

    DeleteUserByEmail.Result deleteUserByEmail(DeleteUserByEmail operation);

    DeleteUserByLogin.Result deleteUserByLogin(DeleteUserByLogin operation);

    BanUserById.Result banUserById(BanUserById operation);

    UnbanUserById.Result unbanUserById(UnbanUserById operation);
}
