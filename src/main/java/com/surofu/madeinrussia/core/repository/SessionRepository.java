package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    Optional<Session> getSessionById(Long id);

    List<Session> getSessionsByUserId(Long userId);

    Optional<Session> getSessionByUserIdAndDeviceId(Long userId, SessionDeviceId deviceId);

    void deleteSessionByUserIdAndDeviceId(Long userId, SessionDeviceId deviceId);

    void save(Session session);

    void deleteSessionById(Long id);

    void deleteByUserId(Long userId);
}
