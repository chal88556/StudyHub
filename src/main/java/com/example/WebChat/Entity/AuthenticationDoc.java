package com.example.WebChat.Entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = "authentication")
public class AuthenticationDoc {
    @Id
    private String id;

    private String studentEmail;
    private String course;
    private String collegeEmail;
    private String idPhotoPath;
    private boolean verified = false;
}
