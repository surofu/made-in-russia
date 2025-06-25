package com.surofu.madeinrussia.infrastructure.persistence.session;

import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface SpringDataSessionRepository extends JpaRepository<Session, Long> {

    @Query("""
            select s from Session s
            where s.user.id = :userId
            order by s.lastModificationDate.value desc
            """)
    List<Session> getAllByUserId(Long userId);

    @Query("""
            select s from Session s
            where s.user.id = :userId and s.deviceId.value = :#{#deviceId.value}
            """)
    @EntityGraph(attributePaths = {"user", "user.vendorDetails"})
    Optional<Session> getSessionByUserIdAndDeviceId(@Param("userId") Long userId, @Param("deviceId") SessionDeviceId deviceId);

    @Modifying
    @Query("""
            delete from Session s
            where s.user.id = :userId and s.deviceId = :deviceId
            """)
    void deleteByUserIdAndDeviceId(Long userId, SessionDeviceId deviceId);
}
