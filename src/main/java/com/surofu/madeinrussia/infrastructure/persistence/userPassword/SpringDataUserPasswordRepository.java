package com.surofu.madeinrussia.infrastructure.persistence.userPassword;

import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.password.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataUserPasswordRepository extends JpaRepository<UserPassword, Long> {
    Optional<UserPassword> findUserPasswordByUserId(Long id);

    Optional<UserPassword> findUserPasswordByUserEmail(UserEmail userEmail);

    @Query("""
            select p from UserPassword p
            join fetch p.user u
            where u.email = :email
            """)
    Optional<UserPassword> findUserPasswordByUserEmailWithUser(@Param("email") UserEmail userEmail);
}
