package com.albrix.Backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.albrix.Backend.entity.Chat;
import com.albrix.Backend.entity.Message;
import com.albrix.Backend.entity.User;
import com.albrix.Backend.repository.ChatRepository;
import com.albrix.Backend.repository.MessageRepository;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatRepository chatRepository;

    @Autowired
    private MessageRepository messageRepository;

    public Chat createChat(Chat chat) {
        return chatRepository.save(chat);
    }

    public Message saveMessage(Chat chat, String role, String content) {
        Message message = new Message();
        message.setChat(chat);
        message.setRole(role);
        message.setContent(content);
        return messageRepository.save(message);
    }


    public List<Message> getMessages(Long chatId) {
        Chat chat = chatRepository.findById(chatId)
                .orElseThrow(() -> new RuntimeException("Chat not found"));

        return messageRepository.findByChat(chat);
    }
    
    public List<Chat> getUserChats(User user) {
        return chatRepository.findByUser(user);
    }

}

