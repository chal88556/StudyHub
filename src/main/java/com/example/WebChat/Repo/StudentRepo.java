package com.example.WebChat.Repo;

import com.example.WebChat.Entity.Student;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudentRepo extends MongoRepository<Student, String> {
    Student findByEmail(String email);
}
