package com.surofu.exporteru.infrastructure.websocket;

import com.surofu.exporteru.application.utils.JwtUtils;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.stereotype.Component;

import java.security.Principal;

/**
 * Интерцептор для аутентификации WebSocket соединений через JWT
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JwtUtils jwtUtils;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(
                message, StompHeaderAccessor.class);

        if (accessor != null && StompCommand.CONNECT.equals(accessor.getCommand())) {
            log.info("=== WebSocket CONNECT attempt ===");
            log.info("Headers: {}", accessor.toNativeHeaderMap());
            
            String token = accessor.getFirstNativeHeader("Authorization");
            log.info("Authorization header: {}", token != null ? "Present (length=" + token.length() + ")" : "MISSING");

            if (token != null && token.startsWith("Bearer ")) {
                token = token.substring(7);

                try {
                    Long userId = jwtUtils.extractUserIdFromAccessToken(token);

                    accessor.setUser(new Principal() {
                        @Override
                        public String getName() {
                            return userId.toString();
                        }
                    });

                    log.info("✅ WebSocket authenticated successfully for user: {}", userId);
                } catch (JwtException | IllegalArgumentException e) {
                    log.error("❌ Invalid JWT token in WebSocket connection: {}", e.getMessage(), e);
                    log.warn("Allowing connection as anonymous for debugging");
                    accessor.setUser(new Principal() {
                        @Override
                        public String getName() {
                            return "anonymous";
                        }
                    });
                }
            } else {
                log.warn("❌ No valid Authorization header in WebSocket CONNECT frame");
                log.warn("Allowing connection as anonymous for debugging");
                accessor.setUser(new Principal() {
                    @Override
                    public String getName() {
                        return "anonymous";
                    }
                });
            }
        }

        return message;
    }
}