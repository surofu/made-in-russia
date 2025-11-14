package com.surofu.exporteru.infrastructure.persistence.user;

import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.core.model.user.UserPhoneNumber;
import com.surofu.exporteru.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaUserRepository implements UserRepository {
    private final SpringDataUserRepository repository;

    @Override
    public Page<User> getPage(Specification<User> specification, Pageable pageable) {
        return repository.findAll(specification, pageable);
    }

    @Override
    public List<User> getByIds(List<Long> ids) {
        return repository.findByIdIn(ids);
    }

    @Override
    public Optional<User> getUserById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<User> getUserByLogin(UserLogin userLogin) {
        return repository.findByLoginValue(userLogin.getValue());
    }

    @Override
    public Optional<User> getUserByEmail(UserEmail userEmail) {
        return repository.findByEmail(userEmail);
    }

    @Override
    public Optional<User> getUserByTelegramUserId(Long telegramUserId) {
        return repository.findFirstByTelegramUserId(telegramUserId);
    }

    @Override
    public Optional<UserEmail> getUserEmailByLogin(UserLogin userLogin) {
        return repository.getUserEmailByLogin(userLogin);
    }

    @Override
    public User save(User user) {
        return repository.save(user);
    }

    @Override
    public boolean existsUserByEmail(UserEmail userEmail) {
        return repository.existsByEmail(userEmail);
    }

    @Override
    public boolean existsUserByLogin(UserLogin userLogin) {
        return repository.existsByLoginValue(userLogin.getValue());
    }

    @Override
    public boolean existsUserByPhoneNumber(UserPhoneNumber userPhoneNumber) {
        return repository.existsByPhoneNumber(userPhoneNumber);
    }

    @Override
    public boolean existsUserByPhoneNumberAndNotUserId(UserPhoneNumber userPhoneNumber, Long id) {
        return repository.existsByPhoneNumberAndIdNot(userPhoneNumber, id);
    }

    @Override
    public Optional<User> getVendorById(Long id) {
        return repository.getVendorById(id);
    }

    @Override
    public boolean existsVendorOrAdminById(Long id) {
        return repository.existsVendorOrAdminById(id);
    }

    @Override
    public void delete(User user) {
        repository.delete(user);
    }

    // View
    @Override
    public Optional<UserView> getViewById(Long id) {
        return repository.findViewById(id);
    }
}
