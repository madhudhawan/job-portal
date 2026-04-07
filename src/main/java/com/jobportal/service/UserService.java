package com.jobportal.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.jobportal.model.User;
import com.jobportal.repository.UserRepository;

@Service
public class UserService {
	
	//inject userRepo to talk with database
	@Autowired
	private UserRepository userRepository;
	
	//inject password encoder it hides the actual password what we set and it shows only like this #123*@!
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	//register new user
	public String registerUser(User user) {
		if(userRepository.existsByEmail(user.getEmail())) {
			return "Email already registered!";
		}
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		
		// Set default status as PENDING
        user.setStatus("ACTIVE");
        
        // Save to database
        userRepository.save(user);

        return "success";
	}
	
	//find by email
	public User findByEmail(String email) {
		Optional<User> result=userRepository.findByEmail(email);
		
		return result.orElse(null);
		
	}
	
	public User findById(Long id) {
		Optional<User> result=userRepository.findById(id);
		return result.orElse(null);
	}
}
