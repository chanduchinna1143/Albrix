package com.albrix.Backend.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.albrix.Backend.entity.User;
import com.albrix.Backend.security.Jwt;
import com.albrix.Backend.service.UserService;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthController {
	
    private final UserService service;
	private final Jwt jwt;
	
	 public AuthController(UserService service , Jwt jwt) {
	        this.service = service;
	        this.jwt = jwt;
	 
	    }

    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody User user) {
        service.register(user);
        return ResponseEntity.status(HttpStatus.CREATED)
                             .body("User registered successfully");
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody User user) {
        User loggedIn = service.login(user.getEmail(), user.getPassword());
        String token =jwt.generateToken(loggedIn.getEmail());
        return ResponseEntity.ok(token);
    }

}
