package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.session.SessionWithUser;

import java.util.List;
import java.util.Optional;

public interface SessionWithUserRepository {
    Optional<SessionWithUser> getSessionById(Long id);

    Optional<SessionWithUser> getSessionByUserIdAndDeviceId(Long id, SessionDeviceId sessionDeviceId);

    List<SessionWithUser> getSessionsByUserId(Long userId);

    void saveOrUpdate(SessionWithUser sessionWithUser);
}
