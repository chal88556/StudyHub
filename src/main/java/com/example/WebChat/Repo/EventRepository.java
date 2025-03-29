package com.example.WebChat.Repo;

import com.example.WebChat.Entity.Event;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface EventRepository extends MongoRepository<Event, String> {
}