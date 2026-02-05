package com.albrix.Backend.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.albrix.Backend.dto.CreateChatRequest;
import com.albrix.Backend.dto.PromptRequest;
import com.albrix.Backend.entity.Chat;
import com.albrix.Backend.entity.Message;
import com.albrix.Backend.entity.User;
import com.albrix.Backend.repository.ChatRepository;
import com.albrix.Backend.repository.UserRepository;
import com.albrix.Backend.security.Jwt;
import com.albrix.Backend.service.ChatService;
import com.albrix.Backend.service.OllamaService;
import com.albrix.Backend.service.PromptBuilderService;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/api/chats")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final Jwt jwt;
    private final PromptBuilderService promptBuilderService;
    private final OllamaService ollamaService;

    public ChatController(
            ChatService chatService,
            UserRepository userRepository,
            ChatRepository chatRepository,
            Jwt jwt,
            PromptBuilderService promptBuilderService,
            OllamaService ollamaService) {

        this.chatService = chatService;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.jwt = jwt;
        this.promptBuilderService = promptBuilderService;
        this.ollamaService = ollamaService;
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(
            @RequestHeader("Authorization") String authHeader,
            @RequestBody CreateChatRequest request) {

        String email = jwt.extractEmail(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chat chat = new Chat();
        chat.setUser(user);
        chat.setProjectType(request.getProjectType());
        chat.setLanguage(request.getLanguage());
        chat.setFramework(request.getFramework());
        chat.setDatabaseType(request.getDatabaseType());
        chat.setOutputType(request.getOutputType());

        return ResponseEntity.ok(chatService.createChat(chat));
    }

    @GetMapping
    public ResponseEntity<List<Chat>> getMyChats(
            @RequestHeader("Authorization") String authHeader) {

        String email = jwt.extractEmail(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        return ResponseEntity.ok(chatService.getUserChats(user));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<List<Message>> getChatHistory(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long chatId) {

        String email = jwt.extractEmail(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        if (!chat.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).build();
        }

        return ResponseEntity.ok(chatService.getMessages(chatId));
    }

    @PostMapping("/{chatId}/message")
    public ResponseEntity<String> sendPrompt(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long chatId,
            @RequestBody PromptRequest request) {

        String email = jwt.extractEmail(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        if (!chat.getUser().getId().equals(user.getId())) {
            return ResponseEntity.status(403).body("Unauthorized");
        }
        chatService.saveMessage(chat, "USER", request.getPrompt());
        List<Message> history = chatService.getMessages(chatId);
        if (history.size() == 1 && chat.getTitle() == null) {
            String title = generateTitle(request.getPrompt());
            chat.setTitle(title);
            chatRepository.save(chat);
        }
        String finalPrompt = promptBuilderService.buildPrompt(
                chat,
                history,
                request.getPrompt()
        );
        String aiResponse = ollamaService.generateBlocking(finalPrompt);
        chatService.saveMessage(chat, "AI", aiResponse);

        return ResponseEntity.ok(aiResponse);
    }
    private String generateTitle(String prompt) {
        String clean = prompt.replaceAll("[^a-zA-Z0-9 ]", "").trim();
        return clean.length() > 50
                ? clean.substring(0, 50) + "..."
                : clean;
    }
    @GetMapping(value = "/{chatId}/stream", produces = "text/event-stream")
    public Flux<String> streamPrompt(
            @RequestHeader("Authorization") String authHeader,
            @PathVariable Long chatId,
            @RequestParam String prompt) {

        String email = jwt.extractEmail(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        if (!chat.getUser().getId().equals(user.getId())) {
            return Flux.error(new RuntimeException("Unauthorized"));
        }

        // Save user message immediately
        chatService.saveMessage(chat, "USER", prompt);

        List<Message> history = chatService.getMessages(chatId);
        String finalPrompt = promptBuilderService.buildPrompt(chat, history, prompt);

        StringBuilder fullResponse = new StringBuilder();

        return ollamaService.generateStream(finalPrompt)
                .doOnNext(token -> fullResponse.append(token))
                .doOnComplete(() -> {
                    // Save AI message when streaming ends
                    chatService.saveMessage(chat, "AI", fullResponse.toString());
                });
    } 
}