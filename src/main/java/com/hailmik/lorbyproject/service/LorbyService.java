package com.hailmik.lorbyproject.service;

import com.hailmik.lorbyproject.dto.RegistrationDTO;
import com.hailmik.lorbyproject.dto.UserDTO;
import com.hailmik.lorbyproject.entity.User;
import com.hailmik.lorbyproject.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class LorbyService {
	private final UserRepository userRepository;
	private final PasswordEncoder encoder;
	private final EmailService emailService;
	public ResponseEntity<String> login(UserDTO user) {
		Optional<User> savedUser = userRepository.findByUsername(user.getUsername());
		ResponseEntity<String> response;
		System.out.println(savedUser.get());
		System.out.println("encoded pword: " + encoder.encode(user.getPassword()));
		if (savedUser.isEmpty()) {
			response = new ResponseEntity<>("Fail", HttpStatus.UNAUTHORIZED);
		} else if (!savedUser.get().isEnabled()) {
			response = new ResponseEntity<>("Unvalidated email", HttpStatus.UNAUTHORIZED);
		}
		else if (encoder.matches(user.getPassword(), savedUser.get().getPassword())) {
			response = new ResponseEntity<>("Success", HttpStatus.OK);
			System.out.println(savedUser);
		}
		else {
			response = new ResponseEntity<>("Fail", HttpStatus.UNAUTHORIZED);
		}
		return response;
	}

	public ResponseEntity<Object> createNewUser(RegistrationDTO user) {
		ResponseEntity<Object> response;
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
			newUser.setEnabled(false);
			Random random = new Random();
			int one = random.nextInt(9);
			int two = random.nextInt(9);
			int three = random.nextInt(9);
			int four = random.nextInt(9);
			String generatedCode = "" + one + two + three + four;
			newUser.setGeneratedCode(generatedCode);
			newUser.setCodeReceived(LocalDateTime.now());
			emailService.sendSimpleMessage(user.getEmail(),
				"Confirmation Code from Lorby",
				generatedCode);
			userRepository.save(newUser);
			response = new ResponseEntity<>(newUser, HttpStatus.CREATED);
		}
		return response;
	}

	public void sendEmail(User user) {
		Random random = new Random();
		int one = random.nextInt(9);
		int two = random.nextInt(9);
		int three = random.nextInt(9);
		int four = random.nextInt(9);
		String generatedCode = "" + one + two + three + four;
		user.setGeneratedCode(generatedCode);
		user.setCodeReceived(LocalDateTime.now());
		userRepository.save(user);
		emailService.sendSimpleMessage(user.getEmail(),
			"Confirmation Code from Lorby",
			generatedCode);
	}


	public User findByEmail(String email) {
		return userRepository.findByEmail(email).get();
	}

	public void updateUser(User user) {
		userRepository.save(user);
	}

	public List<User> findAll() {
		return userRepository.findAll();
	}

	@Transactional
	public User deleteByEmail(String email) {
		User user = userRepository.findByEmail(email).get();
		userRepository.delete(user);
		System.out.println(user);
		return user;
	}

	public void saveUser(User user) {
		userRepository.save(user);
	}
}
