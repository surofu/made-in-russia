package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncMeApplicationService {

    private final UserRepository userRepository;

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateUser(User user) {
        try {
            userRepository.save(user);
        } catch (Exception e) {
            log.error("Error saving updated user: {}", e.getMessage(), e);
        }
    }
}
