package com.example.WebChat.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class EmailService {

    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    @Autowired
    private JavaMailSender mailSender;

    private final Map<String, String> otpStorage = new HashMap<>();

    public void sendEmail(String to, String subject, String body) {
        logger.info("Sending email to: {}", to);

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom("fybsccsa69@gmail.com"); // Replace with your email
        message.setTo(to);
        message.setSubject(subject);
        message.setText(body);

        try {
            mailSender.send(message);
            logger.info("Email sent successfully to: {}", to);
        } catch (Exception e) {
            logger.error("Error sending email to: {}", to, e);
        }
    }

    public String generateOtp(String email) {
        String otp = String.format("%06d", new Random().nextInt(1000000));
        otpStorage.put(email, otp);
        logger.info("Generated OTP {} for email {}", otp, email);
        return otp;
    }

    public boolean verifyOtp(String email, String otp) {
        String storedOtp = otpStorage.get(email);
        logger.info("Verifying OTP for email {}. Stored OTP: {}, Entered OTP: {}", email, storedOtp, otp);
        return otp.equals(storedOtp);
    }
}