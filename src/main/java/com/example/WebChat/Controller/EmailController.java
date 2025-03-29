package com.example.WebChat.Controller;

import com.example.WebChat.Service.EmailService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;


@RestController
@RequestMapping("/api/email")
@CrossOrigin(origins = "http://localhost:3000", allowCredentials = "true")
public class EmailController {

    private static final Logger logger = LoggerFactory.getLogger(EmailController.class);

    @Autowired
    private EmailService emailService;

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> payload) {
        String toEmail = payload.get("email");
        logger.info("Received request to send OTP to: {}", toEmail);

        try {
            String otp = emailService.generateOtp(toEmail);
            emailService.sendEmail(toEmail, "Your OTP code for the StudyHub Website Registration is =", "Your OTP code for the StudyHub Website Registration is = " + otp);
            Map<String, String> response = new HashMap<>();
            response.put("message", "OTP sent successfully");
            response.put("otp", otp); // Include OTP in the response
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error sending OTP to: {}", toEmail, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to send OTP: " + e.getMessage()));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        String otp = payload.get("otp");

        if (emailService.verifyOtp(email, otp)) {
            return ResponseEntity.ok(Map.of("message", "OTP verified successfully"));
        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(Map.of("message", "Invalid OTP"));
        }

    }
    @PostMapping("/subscribe")
    public ResponseEntity<?> subscribe(@RequestBody Map<String, String> payload) {
        String email = payload.get("email");
        logger.info("Received subscription request for email: {}", email); // Log at the beginning
    
        if (email == null || email.isEmpty()) {
            logger.error("Email is null or empty");
            return ResponseEntity.badRequest().body(Map.of("message", "Email is required"));
        }        
    
        try {
            String subject = "Thank you for subscribing to StudyHub!";
            String body = "Thanks! To subscribe our StudyHub website. We will see each other for any query.";
            logger.info("Before sending email in subscribe method"); // Log before sending
            emailService.sendEmail(email, subject, body);
            logger.info("After sending email in subscribe method"); // Log after sending
            return ResponseEntity.ok(Map.of("message", "Subscription email sent successfully"));
        } catch (Exception e) {
            logger.error("Error sending subscription email to: {}", email, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("message", "Failed to send subscription email: " + e.getMessage()));
        }
    }
    }
    
