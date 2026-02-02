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


@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final Jwt jwt;
    private final PromptBuilderService promptBuilderService;
    private final OllamaService ollamaService;

    public ChatController(ChatService chatService,UserRepository userRepository,ChatRepository chatRepository,Jwt jwt,
            PromptBuilderService promptBuilderService,OllamaService ollamaService) {
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.jwt = jwt;
        this.promptBuilderService = promptBuilderService;
        this.ollamaService = ollamaService;
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestHeader("Authorization") String authHeader,@RequestBody CreateChatRequest request){
        String email = jwt.extractEmail(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
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
    public ResponseEntity<List<Chat>> getMyChats(@RequestHeader("Authorization") String authHeader) {
        String email = jwt.extractEmail(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        return ResponseEntity.ok(chatService.getUserChats(user));
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<List<Message>> getChatHistory( @RequestHeader("Authorization") String authHeader,@PathVariable Long chatId){
        String email = jwt.extractEmail(authHeader.replace("Bearer ", ""));
        User user = userRepository.findByEmail(email).orElseThrow(() -> new RuntimeException("User not found"));
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
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

        // Save USER message
        chatService.saveMessage(chat, "USER", request.getPrompt());

        String finalPrompt = promptBuilderService.buildPrompt(chat, request.getPrompt());

        // ✅ BLOCKING AI CALL (SECURITY CONTEXT SAFE)
        String aiResponse = ollamaService.generateBlocking(finalPrompt);

        // Save AI message
        chatService.saveMessage(chat, "AI", aiResponse);

        return ResponseEntity.ok(aiResponse);
    }
}
