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
        return repository.findByUserId(userId);
    }

    @Override
    public Optional<Session> getSessionById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<Session> getSessionByDeviceId(SessionDeviceId deviceId) {
        return repository.findByDeviceId(deviceId);
    }

    @Override
    public void saveOrUpdate(Session session) {
        repository.save(session);
    }
}
