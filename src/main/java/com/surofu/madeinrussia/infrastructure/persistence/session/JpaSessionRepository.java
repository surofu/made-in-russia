package com.surofu.madeinrussia.infrastructure.persistence.session;

import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.repository.SessionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaSessionRepository implements SessionRepository {
    private final SpringDataSessionRepository repository;

    @Override
    public List<Session> getSessionsByUserId(Long userId) {
        return repository.getAllByUserId(userId);
    }

    @Override
    public Optional<Session> getSessionByUserIdAndDeviceId(Long userId, SessionDeviceId deviceId) {
        return repository.getSessionByUserIdAndDeviceId(userId, deviceId);
    }

    @Override
    public void deleteSessionByUserIdAndDeviceId(Long userId, SessionDeviceId deviceId) {
        repository.deleteByUserIdAndDeviceId(userId, deviceId);
    }
}
