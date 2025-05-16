package com.surofu.madeinrussia.infrastructure.persistence.session;

import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import com.surofu.madeinrussia.core.model.session.SessionWithUser;
import com.surofu.madeinrussia.core.repository.SessionWithUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@RequiredArgsConstructor
public class JpaSessionWithUserRepository implements SessionWithUserRepository {
    private final SpringDataSessionWithUserRepository repository;

    @Override
    public Optional<SessionWithUser> getSessionById(Long id) {
        return repository.findById(id);
    }

    @Override
    public Optional<SessionWithUser> getSessionByUserIdAndDeviceId(Long id, SessionDeviceId sessionDeviceId) {
        return repository.findByUserIdAndDeviceId(id, sessionDeviceId);
    }

    @Override
    public void saveOrUpdate(SessionWithUser sessionWithUser) {
        repository.save(sessionWithUser);
    }
}
