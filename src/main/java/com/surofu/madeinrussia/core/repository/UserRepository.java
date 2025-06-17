package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;

import java.util.Optional;

public interface UserRepository {
    Optional<User> getUserById(Long id);

    Optional<User> getUserByLogin(UserLogin userLogin);

    Optional<User> getUserByEmail(UserEmail userEmail);

    Optional<UserEmail> getUserEmailByLogin(UserLogin userLogin);

    User saveUser(User user);

    boolean existsUserByEmail(UserEmail userEmail);

    boolean existsUserByLogin(UserLogin userLogin);

    boolean existsUserByPhoneNumber(UserPhoneNumber userPhoneNumber);

    Optional<User> getVendorById(Long id);

    boolean existsVendorById(Long id);
}
