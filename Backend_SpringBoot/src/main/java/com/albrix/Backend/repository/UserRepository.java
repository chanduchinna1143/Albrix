package com.albrix.Backend.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.albrix.Backend.entity.User;

import java.util.*;

public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByEmail(String email);

}
