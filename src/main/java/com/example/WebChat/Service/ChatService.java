package com.example.WebChat.Service;

import com.example.WebChat.Entity.ChatMessage;
import com.example.WebChat.Repo.ChatMessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ChatService {

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    // Fetch messages with correct order & pagination
    public List<ChatMessage> getMessagesByCourseWithPagination(String courseId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("timestamp").ascending());
        Page<ChatMessage> pagedResult = chatMessageRepository.findByCourseIdOrderByTimestampAsc(courseId, pageable);
        return pagedResult.getContent();
    }

    @Cacheable(value = "messages", key = "#courseId")
    public List<ChatMessage> getMessagesByCourse(String courseId) {
        return chatMessageRepository.findByCourseIdOrderByTimestampAsc(courseId);
    }

    @CachePut(value = "messages", key = "#message.courseId")
    public ChatMessage saveMessage(ChatMessage message) {
        return chatMessageRepository.save(message);
    }

    public ChatMessage findMessageById(String messageId) {
        return chatMessageRepository.findById(messageId).orElse(null);
    }
    public void deleteMessageById(String messageId) {
        chatMessageRepository.deleteById(messageId);
    }
}
