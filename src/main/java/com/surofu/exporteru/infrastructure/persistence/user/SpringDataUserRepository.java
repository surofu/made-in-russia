package com.surofu.exporteru.infrastructure.persistence.user;

import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.model.user.UserEmail;
import com.surofu.exporteru.core.model.user.UserLogin;
import com.surofu.exporteru.core.model.user.UserPhoneNumber;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    @EntityGraph(attributePaths = {"password", "vendorDetails"})
    List<User> findByIdIn(List<Long> ids);

    @Query("select u from User u where u.id = :id")
    @EntityGraph(attributePaths = {"password", "vendorDetails"})
    @NotNull
    Optional<User> findById(@Param("id") @NotNull Long id);

    @EntityGraph(attributePaths = {"password", "vendorDetails"})
    Optional<User> findByLoginValue(String value);

    @EntityGraph(attributePaths = {"password", "vendorDetails"})
    Optional<User> findByEmail(UserEmail userEmail);

    boolean existsByEmail(UserEmail userEmail);

    boolean existsByLoginValue(String value);

    boolean existsByPhoneNumber(UserPhoneNumber userPhoneNumber);

    @Query("""
                select u.email from User u
                where u.login = :login
            """)
    Optional<UserEmail> getUserEmailByLogin(@Param("login") UserLogin login);

    @Query("""
                select u from User u
                where u.id = :id and u.role in ('ROLE_VENDOR', 'ROLE_ADMIN')
            """)
    @EntityGraph(attributePaths = {"password", "vendorDetails"})
    Optional<User> getVendorById(@Param("id") Long id);

    @Query("""
            select count(*) > 0 from User u
            where u.id = :vendorId and u.role in ('ROLE_VENDOR', 'ROLE_ADMIN')
            """)
    boolean existsVendorOrAdminById(@Param("vendorId") Long vendorId);

    @Query("""
            select u from User u
            where u.role = 'ROLE_ADMIN'
            order by u.id asc
            limit 1
            """)
    Optional<User> findFirstAdminUser();

    // View
    Optional<UserView> findViewById(Long id);

    boolean existsByPhoneNumberAndIdNot(UserPhoneNumber phoneNumber, Long id);

    Optional<User> findFirstByTelegramUserId(Long telegramUserId);
}
