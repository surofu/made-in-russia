package com.surofu.madeinrussia.infrastructure.persistence.user;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.model.user.UserEmail;
import com.surofu.madeinrussia.core.model.user.UserLogin;
import com.surofu.madeinrussia.core.model.user.UserPhoneNumber;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataUserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {
    Page<Long> findUserIdsBy(Specification<User> specification, Pageable pageable);

    @EntityGraph(attributePaths = {"vendorDetails", "vendorDetails.vendorCountries", "vendorDetails.vendorProductCategories", "vendorDetails.faq"})
    List<User> findByIdIn(List<Long> ids);

    @Query("select u from User u where u.id = :id")
    @EntityGraph(attributePaths = {"password", "vendorDetails", "vendorDetails.vendorCountries", "vendorDetails.vendorProductCategories", "vendorDetails.faq"})
    Optional<User> findById(@Param("id") Long id);

    @EntityGraph(attributePaths = {"password", "vendorDetails", "vendorDetails.vendorCountries", "vendorDetails.vendorProductCategories", "vendorDetails.faq"})
    Optional<User> findByLogin(UserLogin userLogin);

    @EntityGraph(attributePaths = {"password", "vendorDetails", "vendorDetails.vendorCountries", "vendorDetails.vendorProductCategories", "vendorDetails.faq"})
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
    @EntityGraph(attributePaths = {"password", "vendorDetails", "vendorDetails.vendorCountries", "vendorDetails.vendorProductCategories", "vendorDetails.faq"})
    Optional<User> getVendorById(@Param("id") Long id);

    @Query("""
            select count(*) > 0 from User u
            where u.id = :vendorId and u.role = 'ROLE_VENDOR'
            """)
    boolean existsVendorById(@Param("vendorId") Long vendorId);

    // View
    Optional<UserView> findViewById(Long id);
}
