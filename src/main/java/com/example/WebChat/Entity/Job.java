package com.example.WebChat.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

/**
 * Represents a single job record in MongoDB.
 */
@Document(collection = "Jobs")
public class Job {

    @Id
    private String id;       // MongoDB ID

    private String title;    // e.g., "Software Intern"
    private String company;  // e.g., "Google"
    private String location; // e.g., "New York, NY"
    private String category; // e.g., "IT", "Marketing", etc.
    private String type;     // e.g., "Full-Time", "Part-Time", "Internship", "Remote"
    private String source;   // "Adzuna", "Jooble", "Others"
    private String snippet;  // Short job description
    private String applyLink; // Direct link to apply
    private Date   postedDate; // When the job was fetched or considered posted

    public Job() {}

    // Getters and Setters

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCompany() { return company; }
    public void setCompany(String company) { this.company = company; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public String getSnippet() { return snippet; }
    public void setSnippet(String snippet) { this.snippet = snippet; }

    public String getApplyLink() { return applyLink; }
    public void setApplyLink(String applyLink) { this.applyLink = applyLink; }

    public Date getPostedDate() { return postedDate; }
    public void setPostedDate(Date postedDate) { this.postedDate = postedDate; }
}