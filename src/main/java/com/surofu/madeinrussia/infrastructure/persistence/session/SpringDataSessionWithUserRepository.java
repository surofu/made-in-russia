package com.surofu.madeinrussia.infrastructure.persistence.session;

import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.session.SessionWithUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SpringDataSessionWithUserRepository extends JpaRepository<SessionWithUser, Long> {
    List<SessionWithUser> findByUserId(Long userId);

    Optional<SessionWithUser> findByUserIdAndDeviceId(Long userId, SessionDeviceId deviceId);
}
