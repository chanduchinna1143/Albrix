package com.albrix.Backend.service;

import java.time.Duration;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
@Service
public class OllamaService {

    private final WebClient webClient;

    @Value("${ollama.model}")
    private String model;

    public OllamaService(@Value("${ollama.base-url}") String baseUrl) {
        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .defaultHeader("Content-Type", "application/json")
                .build();
    }
    
    public String generateTitle(String userPrompt) {

        String titlePrompt = """
        Generate a short, clear title (max 6 words) for the following request.
        Do NOT use quotes. Do NOT explain.

        Request:
        %s
        """.formatted(userPrompt);

        return generateBlocking(titlePrompt)
                .replaceAll("[\\n\\r]", "")
                .trim();
    }

    private Mono<String> generateMono(String prompt) {
        Map<String, Object> body = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", false
        );

        return webClient.post()
                .uri("/api/generate")
                .bodyValue(body)
                .retrieve()
                .bodyToMono(OllamaResponse.class)
                .map(OllamaResponse::getResponse);
    }

    public String generateBlocking(String prompt) {
        return generateMono(prompt)
                .block(Duration.ofMinutes(5));
    }
    public Flux<String> generateStream(String prompt) {

        Map<String, Object> body = Map.of(
                "model", model,
                "prompt", prompt,
                "stream", true
        );

        return webClient.post()
                .uri("/api/generate")
                .bodyValue(body)
                .retrieve()
                .bodyToFlux(Map.class)
                .map(chunk -> {
                    Object token = chunk.get("response");
                    return token != null ? token.toString() : "";
                });
    } 

    static class OllamaResponse {
        private String response;
        public String getResponse() { return response; }
        public void setResponse(String response) { this.response = response; }
    }
}