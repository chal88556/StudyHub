package com.example.WebChat;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * Main Application Class.
 */
@SpringBootApplication
@EnableScheduling
public class WebChatApplication {
    public static void main(String[] args) {
        SpringApplication.run(WebChatApplication.class, args);
    }
}
