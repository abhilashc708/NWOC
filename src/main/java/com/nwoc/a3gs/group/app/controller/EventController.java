package com.nwoc.a3gs.group.app.controller;

import com.nwoc.a3gs.group.app.dto.EventDto;
import com.nwoc.a3gs.group.app.model.Event;
import com.nwoc.a3gs.group.app.services.EventService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api")
public class EventController {
    

        @Autowired
        EventService eventService ;
        private static final Logger LOGGER = LogManager.getLogger(EventController.class);

        @PostMapping(value="/events/create",produces= MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> createEvent(@ModelAttribute @Valid EventDto eventDto) {
        try {
            if(eventService.save(eventDto)) {
                return ResponseEntity.ok("Event Insertion successfully.");
            }
            else {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Events Registration Failed.");
            }

        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

        @GetMapping(value="/events/all", produces=MediaType.APPLICATION_JSON_VALUE)
        public List<Event> getAllEvents() {
        return eventService.findAll();
    }

        @GetMapping(value="/events/{event_id}",produces=MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> getEventById(@PathVariable(value = "event_id") Long event_id) {
        try {
            Optional<Event> eventOpt = eventService.findOne(event_id);
            if (!eventOpt.isPresent()) {
                return ((ResponseEntity.BodyBuilder) ResponseEntity.notFound()).body("Event Not Found");
            }
            return ResponseEntity.ok().body(eventOpt);
        }
        catch(Exception e) {

            LOGGER.error(e.getMessage(), e);
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();

        }
    }

        @PostMapping(value="/events/{event_id}",produces=MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> updateEvent(@ModelAttribute @Valid EventDto eventsDTO, @PathVariable(value = "event_id") Long event_id) {
        try {
            if(eventService.update(eventsDTO, event_id))
            {
                return ResponseEntity.ok("Event Updation successfully.");
            }
            else
            {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Events Updation Failed.");
            }

        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

        @DeleteMapping(value="/events/{event_id}",produces=MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<?> deleteEvent(@PathVariable(value = "event_id") Long event_id) {
        try {
            Optional<Event> eventOpt = eventService.findOne(event_id);
            if (!eventOpt.isPresent()) {
                return ((ResponseEntity.BodyBuilder) ResponseEntity.notFound()).body("Event  Not Found");
            }

            eventService.delete(eventOpt.get());
            return ResponseEntity.ok().body(eventOpt.get().getName() + "  Successfully Deleted");
        }
        catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            System.out.println(e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

        @GetMapping(value="/events/list",produces=MediaType.APPLICATION_JSON_VALUE)
        public ResponseEntity<Page<Event>> listEventsByPages(@RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "10") int size) {
        try {

            Page<Event> eventPages = eventService.findEventsByPages(page, size);
            return ResponseEntity.ok(eventPages);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

}
