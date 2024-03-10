package com.hailmik.lorbyproject.service;

import com.hailmik.lorbyproject.dto.RegistrationDTO;
import com.hailmik.lorbyproject.dto.UserDTO;
import com.hailmik.lorbyproject.entity.User;
import com.hailmik.lorbyproject.repo.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LorbyService {
	private final UserRepository userRepository;
	private final PasswordEncoder encoder;
	public ResponseEntity<String> login(UserDTO user) {
		Optional<User> savedUser = userRepository.findByUsername(user.getUsername());
		ResponseEntity<String> response;
		System.out.println(savedUser.get());
		System.out.println("encoded pword: " + encoder.encode(user.getPassword()));
		if (savedUser.isEmpty()) {
			response = new ResponseEntity<>("Fail", HttpStatus.UNAUTHORIZED);
		}
//		else if (savedUser.get().getPassword().equals(encoder.encode(user.getPassword()))) {
//			response = new ResponseEntity<>("Success", HttpStatus.OK);
//			System.out.println(savedUser);
//		}
		else if (encoder.matches(user.getPassword(), savedUser.get().getPassword())) {
			response = new ResponseEntity<>("Success", HttpStatus.OK);
			System.out.println(savedUser);
		}
		else {
			response = new ResponseEntity<>("Fail", HttpStatus.UNAUTHORIZED);
		}
		return response;
	}

	public ResponseEntity<String> createNewUser(RegistrationDTO user) {
		ResponseEntity<String> response;
		Optional<User> savedUserName = userRepository.findByUsername(user.getUsername());
		Optional<User> savedUserEmail = userRepository.findByEmail(user.getEmail());
		if (savedUserEmail.isPresent()) {
			response = new ResponseEntity<>("This email is already taken", HttpStatus.CONFLICT);
		} else if (savedUserName.isPresent()) {
			response = new ResponseEntity<>("This username is already taken", HttpStatus.CONFLICT);
		} else {
			User newUser = new User();
			newUser.setEmail(user.getEmail());
			newUser.setUsername(user.getUsername());
			newUser.setPassword(encoder.encode(user.getPassword()));
			newUser.setEnabled(true);
			userRepository.save(newUser);
			System.out.println(newUser);
			response = new ResponseEntity<>("User created", HttpStatus.CREATED);
		}
		return response;
	}
}
