package com.example.WebChat.Repo;

import com.example.WebChat.Entity.Job;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobRepository extends MongoRepository<Job, String> {
    // Basic CRUD operations are automatically provided by MongoRepository
}