package com.surofu.madeinrussia.infrastructure.persistence.session;

import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataSessionRepository extends JpaRepository<Session, Long> {
    List<Session> findByUserId(Long userId);

    Optional<Session> findByDeviceId(SessionDeviceId deviceId);

    void deleteByDeviceId(SessionDeviceId deviceId);
}
