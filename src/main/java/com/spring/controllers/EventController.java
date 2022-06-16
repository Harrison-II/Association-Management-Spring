package com.spring.ams.controllers;

import com.spring.ams.entity.Event;
import com.spring.ams.entity.User;
import com.spring.ams.repository.UserRepository;
import com.spring.ams.service.EventService;
import com.spring.ams.service.impl.EmailSenderService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
public class EventController {

    private final EventService eventService;

    public EventController(EventService eventService) {
        this.eventService = eventService;
    }

    @GetMapping("/events")
    public String listEvents(Model model){
        model.addAttribute("events", eventService.getAllEvents());
        return "events";
    }

    @GetMapping("/events/new")
    public String createEventForm(Model model){
        Event event = new Event();
        model.addAttribute("event", event);

        return "create_event";
    }

    @PostMapping("/events")
    public String saveEvents(@ModelAttribute("event") Event event){
        eventService.addEvent(event);
        return "redirect:/events";
    }

    @GetMapping("/events/edit/{id}")
    public String editEventForm(@PathVariable Integer id, Model model){
        model.addAttribute("event", eventService.getEVentById(id));
        return "edit_events";
    }

    @PostMapping("/events/{id}")
    public String updateEvent(@PathVariable Integer id, @ModelAttribute("event") Event event, Model model){
        //get event from db by id
        Event existingEvent = eventService.getEVentById(id);

        existingEvent.setId(id);
        existingEvent.setTitle(event.getTitle());
        existingEvent.setEventType(event.getEventType());
        existingEvent.setDesc(event.getDesc());
        existingEvent.setDate(event.getDate());
        existingEvent.setTime(event.getTime());
        existingEvent.setLocation(event.getLocation());

        eventService.updateEvent(existingEvent);
        return "redirect:/events";
    }

    @GetMapping("/events/{id}")
    public String deleteEvent(@PathVariable Integer id){
        eventService.deleteEventById(id);

        return "redirect:/events";
    }

}
