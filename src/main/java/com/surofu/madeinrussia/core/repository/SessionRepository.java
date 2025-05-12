package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    List<Session> getSessionsByUserId(Long userId);

    Optional<Session> getSessionById(Long id);

    Optional<Session> getSessionByDeviceId(SessionDeviceId deviceId);

    void saveOrUpdate(Session session);
}
