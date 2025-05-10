package com.surofu.madeinrussia.infrastructure.persistence.userPassword;

import com.surofu.madeinrussia.core.model.userPassword.UserPassword;
import com.surofu.madeinrussia.core.repository.UserPasswordRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserPasswordRepository implements UserPasswordRepository {
    private final SpringDataUserPasswordRepository repository;

    @Override
    public Optional<UserPassword> getUserPasswordByUserId(Long id) {
        return repository.findUserPasswordByUserId(id);
    }

    @Override
    public void saveUserPassword(UserPassword userPassword) {
        repository.save(userPassword);
    }
}
