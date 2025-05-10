package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.userPassword.UserPassword;

import java.util.Optional;

public interface UserPasswordRepository {
    Optional<UserPassword> getUserPasswordByUserId(Long id);

    void saveUserPassword(UserPassword userPassword);
}
