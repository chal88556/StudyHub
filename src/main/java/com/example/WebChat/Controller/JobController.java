package com.example.WebChat.Controller;

import com.example.WebChat.Entity.Job;
import com.example.WebChat.Service.JobService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = {"http://localhost:3000", "https://localhost:8080"})// Adjust origins as needed for security
public class JobController {

    private final JobService jobService;

    public JobController(JobService jobService) {
        this.jobService = jobService;
    }
    @GetMapping("/jobs")
    public ResponseEntity<List<Job>> getJobs(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String location,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String type
    ) {
        // Step 1: Fetch jobs from DB based on ALL filters
        List<Job> jobs = jobService.getJobs(title, company, location, category, source, type);

        if (jobs.isEmpty()) {
            // Step 2: If no jobs found in DB AND user provided title or location, fetch from APIs
            if ((title != null && !title.isEmpty()) || (location != null && !location.isEmpty())) {
                String keyword = (title != null && !title.isEmpty()) ? title : ""; // Use empty string if title is not provided
                String apiLocation = (location != null && !location.isEmpty()) ? location : ""; // Use empty string if location is not provided

                List<Job> apiJobs = jobService.fetchJobsIfNoResults(keyword, apiLocation);
                if (apiJobs != null && !apiJobs.isEmpty()) {
                    return ResponseEntity.ok(apiJobs);
                }
            }
        }

        // Return jobs from DB or empty list (if both DB and API returned no results)
        return ResponseEntity.ok(jobs);
    }

    
    @DeleteMapping("/jobs/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable String id) {
       
        jobService.removeJob(id);
        return ResponseEntity.noContent().build();
    }
}