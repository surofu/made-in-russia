package com.surofu.madeinrussia.infrastructure.persistence.userPassword;

import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.password.UserPassword;
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
    public Optional<UserPassword> getUserPasswordByUserEmail(UserEmail userEmail) {
        return repository.findUserPasswordByUserEmail(userEmail);
    }

    @Override
    public Optional<UserPassword> getUserPasswordByUserEmailWithUser(UserEmail userEmail) {
        return repository.findUserPasswordByUserEmailWithUser(userEmail);
    }

    @Override
    public void saveUserPassword(UserPassword userPassword) {
        repository.save(userPassword);
    }
}
