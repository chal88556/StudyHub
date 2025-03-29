package com.example.WebChat.Repo;

import com.example.WebChat.Entity.ChatMessage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {

    List<ChatMessage> findByCourseIdOrderByTimestampAsc(String courseId);  //Fetch messages oldest → newest

    Page<ChatMessage> findByCourseIdOrderByTimestampAsc(String courseId, Pageable pageable); 
}
