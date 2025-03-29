package com.example.WebChat.Controller;

import com.example.WebChat.Service.ChatbotService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin(origins = {"http://127.0.0.1:5500", "http://localhost:5500"}) // Add other origins if needed
@RequestMapping("/api/chatbot")
public class ChatbotController {

    @Autowired
    private ChatbotService chatbotService;

    @PostMapping
    public String getResponse(@RequestBody ChatbotRequest request) {
        return chatbotService.getChatbotResponse(request.getMessage());
    }

    public static class ChatbotRequest {
        private String message;

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }
    }
}