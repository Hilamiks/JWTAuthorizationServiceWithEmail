package com.hailmik.lorbyproject.service;

import com.hailmik.lorbyproject.entity.User;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

	private final JavaMailSender emailSender;

	public EmailService(JavaMailSender emailSender) {
		this.emailSender = emailSender;
	}

	public void sendSimpleMessage(
		String to, String subject, String text
	) {
		SimpleMailMessage message = new SimpleMailMessage();
		message.setFrom("lorbymail@gmail.com");
		message.setTo(to);
		message.setSubject(subject);
		message.setText(text);
		emailSender.send(message);

	}
}
