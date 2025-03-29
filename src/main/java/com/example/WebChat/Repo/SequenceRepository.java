package com.example.WebChat.Repo;

import com.example.WebChat.Entity.Sequence;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SequenceRepository extends MongoRepository<Sequence, String> {
}
