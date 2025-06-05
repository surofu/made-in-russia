package com.surofu.madeinrussia.application.service.async;

import com.surofu.madeinrussia.core.model.user.User;
import com.surofu.madeinrussia.core.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@RequiredArgsConstructor
public class AsyncMeApplicationService {

    private final UserRepository userRepository;

    @Async
    public CompletableFuture<Void> updateUser(User user) {
        try {
            userRepository.saveUser(user);
        } catch (Exception e) {
            log.error("Error saving updated user: {}", e.getMessage(), e);
        }

        return CompletableFuture.completedFuture(null);
    }
}
