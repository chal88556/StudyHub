package com.example.WebChat.Service;


import com.example.WebChat.Entity.Event;
import com.example.WebChat.Repo.EventRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EventService {

    @Autowired
    private EventRepository eventRepository;

    public List<Event> getAllEvents() {
        return eventRepository.findAll();
    }
    public Event addEvent(Event event) {
        return eventRepository.save(event);
    }
}