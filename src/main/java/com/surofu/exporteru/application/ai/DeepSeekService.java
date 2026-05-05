package com.surofu.exporteru.application.ai;

import io.jsonwebtoken.lang.Assert;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeepSeekService {
  private final ChatClient chat;

  public String generateResponse(String userPrompt) {
    Assert.hasText(userPrompt, "userPrompt must not be blank");

    log.debug("DeepSeek request: {} chars", userPrompt.length());

    String response = chat.prompt()
        .user(userPrompt)
        .call()
        .content();

    if (response == null || response.isBlank()) {
      throw new RuntimeException("DeepSeek returned empty response");
    }

    log.debug("DeepSeek response: {} chars", response.length());

    return response;
  }
}
