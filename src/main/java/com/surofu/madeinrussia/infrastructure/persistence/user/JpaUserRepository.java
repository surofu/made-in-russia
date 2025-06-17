package com.surofu.madeinrussia.infrastructure.persistence.user;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import com.surofu.madeinrussia.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {
    private final SpringDataUserRepository repository;

    @Override
    public Optional<User> getUserById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<User> getUserByLogin(UserLogin userLogin) {
        return repository.findByLogin(userLogin);
    }

    @Override
    public Optional<User> getUserByEmail(UserEmail userEmail) {
        return repository.findByEmail(userEmail);
    }

    @Override
    public Optional<UserEmail> getUserEmailByLogin(UserLogin userLogin) {
        return repository.getUserEmailByLogin(userLogin);
    }

    @Override
    public User saveUser(User user) {
        return repository.save(user);
    }

    @Override
    public boolean existsUserByEmail(UserEmail userEmail) {
        return repository.existsByEmail(userEmail);
    }

    @Override
    public boolean existsUserByLogin(UserLogin userLogin) {
        return repository.existsByLogin(userLogin);
    }

    @Override
    public boolean existsUserByPhoneNumber(UserPhoneNumber userPhoneNumber) {
        return repository.existsByPhoneNumber(userPhoneNumber);
    }

    @Override
    public Optional<User> getVendorById(Long id) {
        return repository.getVendorById(id);
    }

    @Override
    public boolean existsVendorById(Long id) {
        return repository.existsVendorById(id);
    }
}
