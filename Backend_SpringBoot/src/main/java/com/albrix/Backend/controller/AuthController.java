package com.albrix.Backend.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import com.albrix.Backend.entity.User;
import com.albrix.Backend.service.UserService;

@Controller
@RequestMapping("/api/auth")
@CrossOrigin(origins="*")
public class AuthController {
	@Autowired
	private UserService service;
	
	@PostMapping("/register")
	public User register(@RequestBody User user) {
		System.out.println("Received user: " + user.getEmail());
		return service.register(user);
	}
	
	@PostMapping("/login")
	public User login(@RequestBody User user) {
		return service.login(user.getEmail(), user.getPassword());
	}
	
	

}
