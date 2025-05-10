package com.surofu.madeinrussia.infrastructure.persistence.userPassword;

import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataUserPasswordRepository extends JpaRepository<UserPassword, Long> {
    Optional<UserPassword> findUserPasswordByUserId(Long id);
}
