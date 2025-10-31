package com.surofu.exporteru.infrastructure.persistence.seo;

import com.surofu.exporteru.core.model.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataSeoVendorRepository extends JpaRepository<User, Long> {

    @Query(value = """
            select
            u.id as id,
            u.registration_date as registeredAt,
            u.last_modification_date as updatedAt
            from users u
            where u.is_enabled = :status and u.role = :role
            order by u.id
            """, nativeQuery = true)
    List<SeoVendorView> findAllBy(@Param("status") Boolean status, @Param("role") String role);
}
