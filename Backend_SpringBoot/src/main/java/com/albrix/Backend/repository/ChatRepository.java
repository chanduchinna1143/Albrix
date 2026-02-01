package com.albrix.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.albrix.Backend.entity.Chat;
import com.albrix.Backend.entity.User;

import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {
    List<Chat> findByUser(User user);
}
