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

@RestController
@RequestMapping("/api/chats")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ChatRepository chatRepository;
    private final Jwt jwt;

    public ChatController(ChatService chatService,UserRepository userRepository,ChatRepository chatRepository,Jwt jwt){
        this.chatService = chatService;
        this.userRepository = userRepository;
        this.chatRepository = chatRepository;
        this.jwt = jwt;
    }

    @PostMapping
    public ResponseEntity<Chat> createChat(@RequestHeader("Authorization") String authHeader,@RequestBody CreateChatRequest request){
        String token = authHeader.replace("Bearer ", "");
        String email = jwt.extractEmail(token);
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

    @PostMapping("/{chatId}/message")
    public ResponseEntity<String> sendPrompt(@PathVariable Long chatId, @RequestBody PromptRequest request){
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
        chatService.saveMessage(chat, "USER", request.getPrompt());
        String aiResponse = """
                // Generated Code
                public class HelloWorld {
                    public static void main(String[] args) {
                        System.out.println("Hello from Albrix AI");
                    }
                }
                """;
        chatService.saveMessage(chat, "AI", aiResponse);
        return ResponseEntity.ok(aiResponse);
    }
    @GetMapping("/{chatId}")
    public ResponseEntity<List<Message>> getChatHistory(@PathVariable Long chatId){
        Chat chat = chatRepository.findById(chatId).orElseThrow(() -> new RuntimeException("Chat not found"));
        return ResponseEntity.ok(chatService.getMessages(chat));
    }
}
