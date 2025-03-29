package com.example.WebChat.Repo;

import com.example.WebChat.Entity.Course;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface CourseRepository extends MongoRepository<Course, String> {
    Course findByCourseName(String courseName);
}
