package com.example.WebChat.WebSocket;

import com.example.WebChat.Entity.ChatMessage;
import com.example.WebChat.Repo.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class WebSocketEventListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @MessageMapping("/chat.sendMessage")
    public void sendMessage(ChatMessage message) {
        chatMessageRepository.save(message);
        messagingTemplate.convertAndSend("/topic/messages", message);
    }
}
