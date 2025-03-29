package com.example.WebChat.Controller;

import com.example.WebChat.Entity.ChatMessage;
import com.example.WebChat.Service.ChatService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Date;

@Controller
public class WebSocketController {

    @Autowired
    private ChatService chatService;

    @MessageMapping("/chat/courses/{courseId}")
    @SendTo("/topic/courses/{courseId}")
    public ChatMessage sendCourseMessage(@DestinationVariable String courseId, ChatMessage message) {
        message.setCourseId(courseId);

        //Ensure correct timestamp format
        if (message.getTimestamp() == null) {
            message.setTimestamp(new Date());  // Use Date, NOT String
        }

        if (message.getStatus() == null) {
            message.setStatus("sent");
        }

      
        ChatMessage saved = chatService.saveMessage(message);
        return saved;
    }
}
