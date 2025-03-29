package com.example.WebChat.Service;


import com.example.WebChat.Entity.LeaderboardEntry;
import com.example.WebChat.Repo.LeaderboardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class LeaderboardService {

    @Autowired
    private LeaderboardRepository leaderboardRepository;

    public List<LeaderboardEntry> getLeaderboardByCourse(String course) {
        return leaderboardRepository.findByCourseOrderByTotalAchievementDesc(course);
    }

    public List<LeaderboardEntry> getAllLeaderboardEntries() {
        return leaderboardRepository.findAllByOrderByTotalAchievementDesc();
    }
}