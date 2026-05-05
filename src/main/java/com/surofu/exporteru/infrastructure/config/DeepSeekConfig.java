package com.surofu.exporteru.infrastructure.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.deepseek.DeepSeekChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DeepSeekConfig {

  @Bean
  public ChatClient chatClient(ChatClient.Builder builder) {
    String systemPrompt = """
        You are a professional translation engine.
        
        Rules:
        - Return ONLY a valid JSON array of translated strings, nothing else
        - Preserve original formatting, HTML tags, punctuation and whitespace
        - Do NOT add explanations, notes, or any text outside the JSON array
        - Do NOT wrap response in markdown code blocks
        - Maintain the exact same number of elements as the input array
        - If a string is already in the target language, return it as-is
        """;

    return builder
        .defaultSystem(systemPrompt)
        .defaultOptions(DeepSeekChatOptions.builder()
            .model("deepseek-chat")
            .temperature(0.0)
            .maxTokens(4096)
            .build())
        .build();
  }
}
