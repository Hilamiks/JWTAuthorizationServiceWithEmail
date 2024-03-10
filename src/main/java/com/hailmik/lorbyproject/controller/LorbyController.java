package com.hailmik.lorbyproject.controller;

import com.hailmik.lorbyproject.dto.RegistrationDTO;
import com.hailmik.lorbyproject.dto.UserDTO;
import com.hailmik.lorbyproject.service.LorbyService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class LorbyController {
	private final LorbyService lorbyService;
	@GetMapping("/check")
	public String check() {
		return "Check success";
	}

	@PostMapping("/login")
	public ResponseEntity<String> login(@RequestBody UserDTO user) {
		System.out.println(user.getUsername());
		System.out.println(user.getPassword());
		return lorbyService.login(user);
	}
	@PostMapping("/register")
	public ResponseEntity<String> register(@RequestBody RegistrationDTO user) {
		return lorbyService.createNewUser(user);
	}
	@PostMapping("/hello")
	public String hello() {
		return "hello, world";
	}

}
