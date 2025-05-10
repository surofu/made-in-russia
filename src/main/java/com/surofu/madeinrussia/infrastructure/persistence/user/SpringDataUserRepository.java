package com.surofu.madeinrussia.infrastructure.persistence.user;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<User, Long> {
    Optional<User> findByLogin(UserLogin login);

    Optional<User> findByEmail(UserEmail email);

    boolean existsByEmail(UserEmail email);

    boolean existsByLogin(UserLogin login);

    @Query("""
                select u.email from User u
                where u.login = :login
            """)
    Optional<String> getUserEmailByLogin(UserLogin login);
}
