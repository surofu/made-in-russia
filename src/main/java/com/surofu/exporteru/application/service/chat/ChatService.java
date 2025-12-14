package com.surofu.exporteru.application.service.chat;

import com.surofu.exporteru.application.converter.ChatConverter;
import com.surofu.exporteru.application.dto.chat.ChatDTO;
import com.surofu.exporteru.application.dto.chat.ChatListResponse;
import com.surofu.exporteru.application.exception.AccessDeniedException;
import com.surofu.exporteru.core.model.chat.Chat;
import com.surofu.exporteru.core.model.chat.ChatParticipant;
import com.surofu.exporteru.core.model.chat.ChatRole;
import com.surofu.exporteru.core.model.product.Product;
import com.surofu.exporteru.core.model.user.User;
import com.surofu.exporteru.core.repository.ProductRepository;
import com.surofu.exporteru.core.repository.UserRepository;
import com.surofu.exporteru.infrastructure.persistence.chat.ChatParticipantRepository;
import com.surofu.exporteru.infrastructure.persistence.chat.ChatRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Сервис для работы с чатами
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ChatService {

    private final ChatRepository chatRepository;
    private final ChatParticipantRepository participantRepository;
    private final ProductRepository productRepository;
    private final UserRepository userRepository;
    private final ChatConverter chatConverter;


    /**
     * Создать чат для товара при отправке первого сообщения
     * Для каждой пары покупатель-продавец создается отдельный чат
     */
    @Transactional
    public ChatDTO createOrGetChat(Long productId, Long buyerId) {
        Product product = productRepository.getById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        Long sellerId = product.getUser().getId();
        if (sellerId.equals(buyerId)) {
            throw new IllegalArgumentException("Seller cannot create chat with themselves");
        }

        Optional<Chat> existingChat = chatRepository.findByProductIdAndBuyerId(productId, buyerId);
        if (existingChat.isPresent()) {
            Chat chat = existingChat.get();
            log.debug("Found existing chat {} for product {} and buyer {}", chat.getId(), productId, buyerId);
            return chatConverter.toDTO(chat, buyerId);
        }

        Chat chat = new Chat();
        chat.setProduct(product);
        chat = chatRepository.save(chat);

        addParticipant(chat, buyerId, ChatRole.BUYER);
        addParticipant(chat, sellerId, ChatRole.SELLER);

        addAllAdminsToChat(chat, buyerId, sellerId);

        log.info("Created new chat {} for product {} with buyer {}", chat.getId(), productId, buyerId);

        Chat chatWithParticipants = chatRepository.findByIdWithParticipants(chat.getId())
                .orElse(chat);

        return chatConverter.toDTO(chatWithParticipants, buyerId);
    }

    /**
     * Получить все чаты пользователя с пагинацией
     */
    @Transactional(readOnly = true)
    public ChatListResponse getUserChats(Long userId, Pageable pageable) {
        Page<Chat> chatsPage = chatRepository.findByParticipantsUserId(userId, pageable);

        List<ChatDTO> chatDTOs = chatsPage.getContent().stream()
                .map(chat -> chatConverter.toDTO(chat, userId))
                .collect(Collectors.toList());

        return ChatListResponse.builder()
                .chats(chatDTOs)
                .totalElements(chatsPage.getTotalElements())
                .totalPages(chatsPage.getTotalPages())
                .currentPage(chatsPage.getNumber())
                .hasMore(chatsPage.hasNext())
                .build();
    }

    /**
     * Получить детали чата
     */
    @Transactional(readOnly = true)
    public ChatDTO getChatDetails(Long chatId, Long userId) {
        Chat chat = chatRepository.findByIdAndParticipantUserId(chatId, userId)
                .orElseThrow(() -> new AccessDeniedException("Access denied to this chat"));

        return chatConverter.toDTO(chat, userId);
    }

    /**
     * Получить все чаты для товара продавца с пагинацией
     */
    @Transactional(readOnly = true)
    public ChatListResponse getProductChats(Long productId, Long userId, Pageable pageable) {
        Product product = productRepository.getById(productId)
                .orElseThrow(() -> new EntityNotFoundException("Product not found with id: " + productId));

        if (!product.getUser().getId().equals(userId)) {
            throw new AccessDeniedException("You can only view chats for your own products");
        }

        Page<Chat> chatsPage = chatRepository.findByProductId(productId, pageable);

        List<ChatDTO> chatDTOs = chatsPage.getContent().stream()
                .map(chat -> chatConverter.toDTO(chat, userId))
                .collect(Collectors.toList());

        log.debug("Found {} chats for product {} (seller {})", chatDTOs.size(), productId, userId);

        return ChatListResponse.builder()
                .chats(chatDTOs)
                .totalElements(chatsPage.getTotalElements())
                .totalPages(chatsPage.getTotalPages())
                .currentPage(chatsPage.getNumber())
                .hasMore(chatsPage.hasNext())
                .build();
    }

    /**
     * Добавить участника в чат
     */
    private void addParticipant(Chat chat, Long userId, ChatRole role) {
        User user = userRepository.getById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id: " + userId));

        ChatParticipant participant = new ChatParticipant();
        participant.setChat(chat);
        participant.setUser(user);
        participant.setRole(role);
        participantRepository.save(participant);

        log.debug("Added participant {} with role {} to chat {}", userId, role, chat.getId());
    }

    /**
     * Получить ID админа для чатов
     */
    private Long getAdminUserId() {
        return userRepository.getFirstAdminUser()
                .map(User::getId)
                .orElse(null);
    }

    /**
     * Добавить всех админов в чат
     */
    private void addAllAdminsToChat(Chat chat, Long buyerId, Long sellerId) {
        List<User> admins = userRepository.getAllAdminUsers();
        for (User admin : admins) {
            Long adminId = admin.getId();

            if (!adminId.equals(buyerId) && !adminId.equals(sellerId)) {
                addParticipant(chat, adminId, ChatRole.ADMIN);
                log.debug("Added admin {} to chat {}", adminId, chat.getId());
            }
        }
    }
}