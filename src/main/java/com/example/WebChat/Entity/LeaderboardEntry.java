package com.example.WebChat.Entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "leaderboard")
public class LeaderboardEntry {

    @Id
    private String id;

    @Field("rollNo") // Map to the "rollNo" field in MongoDB
    private String rollNo;

    @Field("studentname") // Map to "studentname"
    private String studentName; // Changed to camelCase

    private String course;

    @Field("totalAchievment") // Map to "totalAchievment"
    private int totalAchievement; // Changed to camelCase and int

  
    private int rank;
}