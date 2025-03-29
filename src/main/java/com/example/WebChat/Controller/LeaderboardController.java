package com.example.WebChat.Controller;

import com.example.WebChat.Entity.LeaderboardEntry;
import com.example.WebChat.Service.LeaderboardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController // Important: Use @RestController for JSON responses
public class LeaderboardController {

    @Autowired
    private LeaderboardService leaderboardService;

    @GetMapping("/api/leaderboard") // Use a different endpoint, e.g., /api/leaderboard
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboardData(
            @RequestParam(name = "course", required = false) String course) {

        List<LeaderboardEntry> leaderboard;

        if (course != null && !course.isEmpty()) {
            leaderboard = leaderboardService.getLeaderboardByCourse(course);
        } else {
            leaderboard = leaderboardService.getAllLeaderboardEntries();
        }
        if (leaderboard != null) {
            for (int i = 0; i < leaderboard.size(); i++) {
                leaderboard.get(i).setRank(i + 1);
            }
        }

        return ResponseEntity.ok(leaderboard); // Return as JSON
    }
}