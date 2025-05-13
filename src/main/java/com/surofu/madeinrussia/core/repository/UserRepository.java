package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserById(Long id);

    Optional<User> getUserByLogin(UserLogin userLogin);

    Optional<User> getUserByEmail(UserEmail userEmail);

    Optional<UserEmail> getUserEmailByLogin(UserLogin userLogin);

    void saveUser(User user);

    boolean existsUserByEmail(UserEmail userEmail);

    boolean existsUserByLogin(UserLogin userLogin);
}
