package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import com.surofu.madeinrussia.infrastructure.persistence.user.UserView;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

public interface UserRepository {
    Page<User> getPage(Specification<User> specification, Pageable pageable);

    List<User> getByIds(List<Long> ids);

    Optional<User> getUserById(Long id);

    Optional<User> getUserByLogin(UserLogin userLogin);

    Optional<User> getUserByEmail(UserEmail userEmail);

    Optional<UserEmail> getUserEmailByLogin(UserLogin userLogin);

    User save(User user);

    boolean existsUserByEmail(UserEmail userEmail);

    boolean existsUserByLogin(UserLogin userLogin);

    boolean existsUserByPhoneNumber(UserPhoneNumber userPhoneNumber);

    Optional<User> getVendorById(Long id);

    boolean existsVendorOrAdminById(Long id);

    void delete(User user);

    // View
    Optional<UserView> getViewById(Long id);
}
