package com.example.WebChat.Entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Data
@Document(collection = "chat_messages")
public class ChatMessage {

    @Id
    private String id;

    private String courseId;
    private String sender;
    private String content;
    private String image;

    @CreatedDate
    private Date timestamp;  // Store as Date instead of String

    private Map<String, String> reactions = new HashMap<>();
    private String status;
}
