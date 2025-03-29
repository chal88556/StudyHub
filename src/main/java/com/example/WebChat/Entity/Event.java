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
@Document(collection = "events") // Or your events collection name
public class Event {
    @Id
    private String id;

    @Field("Event ID") // Match EXACTLY as in MongoDB, including spaces
    private Integer eventId; // Use Integer, not int

    @Field("Event Name") // Match EXACTLY as in MongoDB, including spaces
    private String eventName;

    @Field("Event Description")
    private String eventDescription;

    @Field("Field of Study")
    private String fieldOfStudy;

    // Use @Field for ALL fields that don't follow standard camelCase
    @Field("Contact Person")
    private String contactPerson;

    @Field("Contact Email")
    private String contactEmail;

    @Field("Registration Link")
    private String registrationLink;

    @Field("Event Type")
    private String eventType;

    @Field("Target Audience") // CamelCase is fine here since it matches
    private String targetAudience;

    @Field("EventImages")
    private String eventImages;

    @Field("Location") // Use @Field even if camelCase, for consistency
    private String location;

    @Field("Time")
    private String time;

    @Field("Duration")
    private String duration;
     
    @Field("Date")
    private String date; //  String is fine for now.  Consider java.time.LocalDate

    @Field("Status")
    private String status;

    @Field("Notes")
    private String notes;
}