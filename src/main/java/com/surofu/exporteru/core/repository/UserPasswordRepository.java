package com.surofu.exporteru.core.repository;

import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.model.user.password.UserPassword;

import java.util.Optional;

public interface UserPasswordRepository {
    Optional<UserPassword> getUserPasswordByUserId(Long id);

    Optional<UserPassword> getUserPasswordByUserEmail(UserEmail userEmail);

    Optional<UserPassword> getUserPasswordByUserEmailWithUser(UserEmail userEmail);

    void saveUserPassword(UserPassword userPassword);
}
