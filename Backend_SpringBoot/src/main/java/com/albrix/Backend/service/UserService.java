package com.albrix.Backend.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.albrix.Backend.entity.User;
import com.albrix.Backend.exception.UserAlreadyExistsException;
import com.albrix.Backend.exception.UserNotFoundException;
import com.albrix.Backend.repository.UserRepository;

@Service
public class UserService {
	
	@Autowired
	private UserRepository repo;
	
    public User register(User user) {
    	if(repo.findByEmail(user.getEmail()).isPresent()) {
    		throw new UserAlreadyExistsException("Email Already Exist");
    	}
    	return repo.save(user);
    }
    
    public User login(String email,String password) {
    	User user = repo.findByEmail(email).orElseThrow(()-> new UserNotFoundException("User Not Found"));
    	if(!user.getPassword().equals(password)) {
    		throw new RuntimeException("Invalid Password");
    	}
    	return user;	
    }

}
