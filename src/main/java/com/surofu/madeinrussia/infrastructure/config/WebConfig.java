package com.surofu.madeinrussia.infrastructure.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.surofu.madeinrussia.application.components.telegrambot.TelegramBot;
import com.surofu.madeinrussia.application.components.telegrambot.TelegramBotLinkAccountHandler;
import com.surofu.madeinrussia.application.components.telegrambot.TelegramBotLoginHandler;
import com.surofu.madeinrussia.application.components.telegrambot.TelegramBotRegisterHandler;
import com.surofu.madeinrussia.application.utils.LocalizationManager;
import com.surofu.madeinrussia.core.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.web.client.RestClient;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;

@Configuration
@EnableSpringDataWebSupport(pageSerializationMode = EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
public class WebConfig {
    @Value("${app.yandex.translate.uri}")
    private String yandexTranslateUri;
    @Value("${app.okved.uri}")
    private String okvedUri;

    @Bean
    public RestClient yandexTranslatorRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(yandexTranslateUri)
                .build();
    }

    @Bean
    public RestClient okvedRestClient(RestClient.Builder builder) {
        return builder
                .baseUrl(okvedUri)
                .build();
    }

    @Bean
    public ObjectMapper objectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        return mapper;
    }

    @Bean
    public TelegramLongPollingBot myTelegramBot(
            TelegramBotConfig config,
            LocalizationManager localizationManager,
            @Lazy TelegramBotRegisterHandler telegramBotRegisterHandler,
            @Lazy TelegramBotLoginHandler telegramBotLoginHandler,
            @Lazy TelegramBotLinkAccountHandler telegramBotLinkAccountHandler,
            UserRepository userRepository
    ) {
        return new TelegramBot(
                config,
                localizationManager,
                telegramBotRegisterHandler,
                telegramBotLoginHandler,
                telegramBotLinkAccountHandler,
                userRepository
        );
    }
}
