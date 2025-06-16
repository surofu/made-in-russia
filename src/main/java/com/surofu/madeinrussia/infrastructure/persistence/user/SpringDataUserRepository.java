package com.surofu.madeinrussia.infrastructure.persistence.user;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<User, Long> {

    @Query("select u from User u where u.id = :id")
    @EntityGraph(attributePaths = {"vendorDetails", "vendorDetails.vendorCountries", "vendorDetails.vendorProductCategories"})
    Optional<User> findById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"vendorDetails", "vendorDetails.vendorCountries", "vendorDetails.vendorProductCategories"})
    Optional<User> findByLogin(UserLogin userLogin);

    @EntityGraph(attributePaths = {"vendorDetails", "vendorDetails.vendorCountries", "vendorDetails.vendorProductCategories"})
    Optional<User> findByEmail(UserEmail userEmail);

    boolean existsByEmail(UserEmail userEmail);

    boolean existsByLogin(UserLogin userLogin);

    boolean existsByPhoneNumber(UserPhoneNumber userPhoneNumber);

    @Query("""
                select u.email from User u
                where u.login = :login
            """)
    Optional<UserEmail> getUserEmailByLogin(@Param("login") UserLogin login);

    @Query("""
                select u from User u
                where u.id = :id and u.role = 'ROLE_VENDOR'
            """)
    Optional<User> getVendorById(@Param("id") Long id);

    @Query("""
            select count(*) > 0 from User u
            where u.id = :vendorId and u.role = 'ROLE_VENDOR'
            """)
    boolean existsVendorById(@Param("vendorId") Long vendorId);
}
