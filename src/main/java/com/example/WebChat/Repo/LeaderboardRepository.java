package com.example.WebChat.Repo;

import com.example.WebChat.Entity.LeaderboardEntry;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;




public interface LeaderboardRepository extends MongoRepository<LeaderboardEntry, String> {

    // Use @Query for more complex queries, especially when field names don't match
    @Query("{ 'course' : ?0 }")
    List<LeaderboardEntry> findByCourseOrderByTotalAchievementDesc(String course);
    
    List<LeaderboardEntry> findAllByOrderByTotalAchievementDesc();
}
