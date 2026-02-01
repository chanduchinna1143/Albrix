package com.albrix.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.albrix.Backend.entity.Message;
import com.albrix.Backend.entity.Chat;

import java.util.List;

public interface MessageRepository extends JpaRepository<Message, Long> {
    List<Message> findByChat(Chat chat);
}
