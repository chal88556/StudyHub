package com.example.WebChat.Repo;

import com.example.WebChat.Entity.AuthenticationDoc;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface AuthenticationRepo extends MongoRepository<AuthenticationDoc, String> {
    AuthenticationDoc findByStudentEmail(String studentEmail);
}
