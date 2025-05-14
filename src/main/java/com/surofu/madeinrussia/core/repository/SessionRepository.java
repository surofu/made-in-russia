package com.surofu.madeinrussia.core.repository;

import com.surofu.madeinrussia.core.model.session.Session;
import com.surofu.madeinrussia.core.model.session.SessionDeviceId;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface SessionRepository {
    List<Session> getSessionsByUserId(Long userId);

    @Query("""
            select s from Session s
            where s.deviceId.deviceId = #{deviceId.deviceId}
            """)
    Optional<Session> getSessionByDeviceId(SessionDeviceId deviceId);

    void saveOrUpdate(Session session);

    @Query("""
            delete from Session s
            where s.deviceId.deviceId = #{deviceId.deviceId}
            """)
    void deleteByDeviceId(SessionDeviceId deviceId);
}
