package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.session.SessionWithUser;

import java.util.Optional;

public interface SessionWithUserRepository {
    Optional<SessionWithUser> getSessionById(Long id);

    Optional<SessionWithUser> getSessionByUserIdAndDeviceId(Long id, SessionDeviceId sessionDeviceId);

    void saveOrUpdate(SessionWithUser sessionWithUser);
}
