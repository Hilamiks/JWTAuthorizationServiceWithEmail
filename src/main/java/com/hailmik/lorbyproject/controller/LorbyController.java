package com.hailmik.lorbyproject.controller;

import com.hailmik.lorbyproject.dto.ConfirmationDTO;
import com.hailmik.lorbyproject.dto.RegistrationDTO;
import com.hailmik.lorbyproject.dto.UserDTO;
import com.hailmik.lorbyproject.entity.User;
import com.hailmik.lorbyproject.service.LorbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
public class LorbyController {
	private final LorbyService lorbyService;
	@GetMapping("/check")
	public String check() {
		return "Check success";
	}

	@GetMapping("/user/{email}")
	public User getUser(@PathVariable String email) {
		return lorbyService.findByEmail(email);
	}
	@GetMapping("/auth/users")
	public List<User> getUsers() {
		return lorbyService.findAll();
	}
	@DeleteMapping("/delete/{email}")
	public User deleteUser(@PathVariable String email) {
		return lorbyService.deleteByEmail(email);
	}

	@PostMapping("/auth/login")
	public ResponseEntity<Object> login(@RequestBody UserDTO user) {
		return lorbyService.login(user);
	}
	@PostMapping("/auth/register")
	public ResponseEntity<Object> register(@RequestBody RegistrationDTO user) {
		if (lorbyService.validates(user)) {
			return lorbyService.createNewUser(user);
		}
		return ResponseEntity.badRequest().body("Invalid data sent");
	}
	@PostMapping("/auth/confirm/{email}")
	public void confirm(@PathVariable String email) {
		User user = lorbyService.findByEmail(email);
		if (user.getCodeReceived().minusMinutes(15L).isAfter(LocalDateTime.now())) {
			lorbyService.sendEmail(user);
		}
	}

	@PostMapping("/auth/code")
	public ResponseEntity<Object> confirmCode(@RequestBody ConfirmationDTO confirmation) {
		String code = confirmation.getCode();
		String email = confirmation.getEmail();
		User user = lorbyService.findByEmail(email);
		System.out.println(user);
		LocalDateTime current = LocalDateTime.now();
		ResponseEntity<Object> response;
		if (current.minusMinutes(15).isBefore(user.getCodeReceived())) {
			if (code.equals(user.getGeneratedCode())) {
				response = new ResponseEntity<>(user, HttpStatus.OK);
				user.setEnabled(true);
				lorbyService.saveUser(user);
			} else {
				response = new ResponseEntity<>("Fail", HttpStatus.UNAUTHORIZED);
			}
		} else {
			response = new ResponseEntity<>("Fail", HttpStatus.GATEWAY_TIMEOUT);
		}
		return response;
	}
}
