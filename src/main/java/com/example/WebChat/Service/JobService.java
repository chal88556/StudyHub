package com.example.WebChat.Service;

import com.example.WebChat.Entity.Job;
import com.example.WebChat.Repo.JobRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class JobService {

    // -----------------------------
    //  API CREDENTIALS from application.properties
    // -----------------------------
    @Value("${adzuna.app.id}")
    private String ADZUNA_APP_ID;

    @Value("${adzuna.app.key}")
    private String ADZUNA_APP_KEY;

    @Value("${jooble.api.key}")
    private String JOOBLE_API_KEY;

    private final JobRepository jobRepository;
    private final MongoTemplate mongoTemplate;
    private final RestTemplate restTemplate = new RestTemplate();

    @Autowired
    public JobService(JobRepository jobRepository, MongoTemplate mongoTemplate) {
        this.jobRepository = jobRepository;
        this.mongoTemplate = mongoTemplate;
    }

    
    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduledFetchAndSaveJobs() {
        System.out.println("[SCHEDULED] Fetching jobs from Adzuna and Jooble...");

        List<Job> adzunaJobs = fetchFromAdzuna("internship", "New York"); 
        List<Job> joobleJobs = fetchFromJooble("internship", "India");

        List<Job> combinedJobs = new ArrayList<>();
        combinedJobs.addAll(adzunaJobs);
        combinedJobs.addAll(joobleJobs);

        for (Job job : combinedJobs) {
            // Check if a similar job already exists (based on title, company, and source)
            Query query = new Query();
            query.addCriteria(Criteria.where("title").is(job.getTitle())
                    .and("company").is(job.getCompany())
                    .and("source").is(job.getSource()));
            Job existingJob = mongoTemplate.findOne(query, Job.class);

            if (existingJob == null) {
                // New job, save it
                jobRepository.save(job);
                System.out.println("[SCHEDULED] Saved new job: " + job.getTitle());
            } else {
                // Job already exists, update the relevant fields
                existingJob.setLocation(job.getLocation());
                existingJob.setCategory(job.getCategory());
                existingJob.setType(job.getType());
                existingJob.setSnippet(job.getSnippet());
                existingJob.setApplyLink(job.getApplyLink());
                existingJob.setPostedDate(job.getPostedDate()); 

                jobRepository.save(existingJob);
                System.out.println("[SCHEDULED] Updated existing job: " + job.getTitle());
            }
        }

        System.out.println("[SCHEDULED] Finished processing jobs.");
    }

    
    private List<Job> fetchFromAdzuna(String keyword, String location) {
        List<Job> results = new ArrayList<>();
        try {
            String encodedKeyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
            String encodedLocation = URLEncoder.encode(location, StandardCharsets.UTF_8);

            // Fetch the first page
            String url = String.format(
                    "https://api.adzuna.com/v1/api/jobs/us/search/1" +
                            "?app_id=%s" +
                            "&app_key=%s" +
                            "&what=%s" +
                            "&where=%s" +
                            "&content-type=application/json",
                    ADZUNA_APP_ID, ADZUNA_APP_KEY, encodedKeyword, encodedLocation
            );

            ResponseEntity<Map> response = restTemplate.getForEntity(url, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> body = response.getBody();
                List<Map<String, Object>> rawJobs = (List<Map<String, Object>>) body.get("results");
                if (rawJobs != null) {
                    Date now = new Date();
                    for (Map<String, Object> jobMap : rawJobs) {
                        Job job = new Job();
                        job.setSource("Adzuna");
                        job.setPostedDate(now);

                        job.setTitle((String) jobMap.getOrDefault("title", "Untitled"));

                        Map<String, Object> companyMap = (Map<String, Object>) jobMap.get("company");
                        if (companyMap != null) {
                            job.setCompany((String) companyMap.getOrDefault("display_name", "Unknown"));
                        }

                        Map<String, Object> locMap = (Map<String, Object>) jobMap.get("location");
                        if (locMap != null) {
                            job.setLocation((String) locMap.getOrDefault("display_name", "Unspecified"));
                        }

                        job.setSnippet((String) jobMap.getOrDefault("description", ""));
                        job.setApplyLink((String) jobMap.getOrDefault("redirect_url", ""));

                        // Adzuna doesn't always provide category/type
                        job.setCategory(null);
                        job.setType(null);

                        results.add(job);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[Adzuna Error] " + e.getMessage());
            // Consider logging to a file or monitoring system
        }
        return results;
    }

    
    private List<Job> fetchFromJooble(String keyword, String location) {
        List<Job> results = new ArrayList<>();
        try {
            String url = "https://jooble.org/api/" + JOOBLE_API_KEY;

            Map<String, String> body = new HashMap<>();
            body.put("keywords", keyword);
            body.put("location", location);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Map<String, String>> requestEntity = new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.postForEntity(url, requestEntity, Map.class);
            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                Map<String, Object> responseMap = response.getBody();
                List<Map<String, Object>> rawJobs = (List<Map<String, Object>>) responseMap.get("jobs");
                if (rawJobs != null) {
                    Date now = new Date();
                    for (Map<String, Object> jobObj : rawJobs) {
                        Job job = new Job();
                        job.setSource("Jooble");
                        job.setPostedDate(now);

                        job.setTitle((String) jobObj.getOrDefault("title", "Untitled"));
                        job.setCompany((String) jobObj.getOrDefault("company", "Unknown"));
                        job.setLocation((String) jobObj.getOrDefault("location", "Unspecified"));
                        job.setSnippet((String) jobObj.getOrDefault("snippet", ""));
                        job.setApplyLink((String) jobObj.getOrDefault("link", ""));

                        
                        job.setCategory(null);
                        job.setType(null);

                        results.add(job);
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("[Jooble Error] " + e.getMessage());
            // Consider logging to a file or monitoring system
        }
        return results;
    }

    
    public List<Job> fetchJobsIfNoResults(String keyword, String location) {
        System.out.println("[ON-DEMAND] Fetching from APIs...");
        List<Job> adzunaJobs = fetchFromAdzuna(keyword, location);
        List<Job> joobleJobs = fetchFromJooble(keyword, location);

        List<Job> combinedJobs = new ArrayList<>();
        combinedJobs.addAll(adzunaJobs);
        combinedJobs.addAll(joobleJobs);

        System.out.println("[ON-DEMAND] Fetched " + combinedJobs.size() + " jobs from APIs.");
        return combinedJobs;
    }

    /**
     * Retrieves jobs from the database based on filters.
     */
    public List<Job> getJobs(String title, String company, String location, String category, String source, String type) {
       
        Query query = new Query();
        List<Criteria> criteriaList = new ArrayList<>();

        if (title != null && !title.isEmpty()) {
            criteriaList.add(Criteria.where("title").regex(title, "i")); // Case-insensitive
        }
        if (company != null && !company.isEmpty()) {
            criteriaList.add(Criteria.where("company").regex(company, "i"));
        }
        if (location != null && !location.isEmpty()) {
            criteriaList.add(Criteria.where("location").regex(location, "i"));
        }
        if (category != null && !category.isEmpty()) {
            criteriaList.add(Criteria.where("category").regex(category, "i"));
        }
        if (source != null && !source.isEmpty()) {
            criteriaList.add(Criteria.where("source").regex(source, "i"));
        }
        if (type != null && !type.isEmpty()) {
            criteriaList.add(Criteria.where("type").regex(type, "i"));
        }

        if (!criteriaList.isEmpty()) {
            query.addCriteria(new Criteria().andOperator(criteriaList.toArray(new Criteria[0])));
        }

        return mongoTemplate.find(query, Job.class);
    }

    
    public void removeJob(String jobId) {
        jobRepository.deleteById(jobId);
    }
}