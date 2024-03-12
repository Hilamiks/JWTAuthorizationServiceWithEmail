package com.hailmik.lorbyproject.service;

import com.hailmik.lorbyproject.dto.AuthenticationResponse;
import com.hailmik.lorbyproject.dto.RegistrationDTO;
import com.hailmik.lorbyproject.dto.UserDTO;
import com.hailmik.lorbyproject.entity.User;
import com.hailmik.lorbyproject.repo.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	public ResponseEntity<Object> login(UserDTO user) {
		System.out.println(user.getUsername());
		System.out.println(user.getPassword());
		authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(
				user.getUsername(),
				user.getPassword()
			)
		);

		User savedUser = userRepository.findByUsername(user.getUsername())
			.orElseThrow();

		String token = jwtService.generateToken(savedUser);

		return ResponseEntity.ok(new AuthenticationResponse(token));
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
			response = new ResponseEntity<>(newUser.getEmail(), HttpStatus.CREATED);
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
		return user;
	}

	public void saveUser(User user) {
		userRepository.save(user);
	}

	public boolean validates(RegistrationDTO user) {
		String email = user.getEmail();
		String username = user.getUsername();
		String password = user.getPassword();

		return email != null
			&& username != null
			&& password != null
			&& !email.isBlank()
			&& email.contains("@")
			&& email.contains(".")
			&& !username.isBlank()
			&& !password.isBlank();
	}
}
