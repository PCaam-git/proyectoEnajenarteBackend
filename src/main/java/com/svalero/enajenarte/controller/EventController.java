package com.svalero.enajenarte.controller;

import com.svalero.enajenarte.dto.EventInDto;
import com.svalero.enajenarte.dto.EventOutDto;
import com.svalero.enajenarte.exception.ErrorResponse;
import com.svalero.enajenarte.exception.EventNotFoundException;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.service.EventService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class EventController {

    @Autowired
    private EventService eventService;

    // GET
    @GetMapping("/events")
    public ResponseEntity<List<EventOutDto>> getAll(
            @RequestParam(value = "title", defaultValue = "") String title,
            @RequestParam(value = "location", defaultValue = "") String location,
            @RequestParam(value = "isPublic", defaultValue = "") String isPublic) {

        List<EventOutDto> eventsOutDto = eventService.findAll(title, location, isPublic);

        // Si la lista está vacía, devuelve 204 No Content
        if (eventsOutDto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // Si hay resultados, devuelve 200 Ok con la lista
        return ResponseEntity.ok(eventsOutDto);
    }

    // GET by id
    @GetMapping("/events/{id}")
    public ResponseEntity<EventOutDto> get(@PathVariable long id) throws EventNotFoundException {
        EventOutDto eventOutDto = eventService.findById(id);
        return ResponseEntity.ok(eventOutDto);
    }

    // POST
    @PostMapping("/events")
    public ResponseEntity<EventOutDto> addEvent(@Valid @RequestBody EventInDto eventInDto) throws SpeakerNotFoundException {
        EventOutDto newEvent = eventService.add(eventInDto);
        return new ResponseEntity<>(newEvent, HttpStatus.CREATED);
    }

    // PUT
    @PutMapping("/events/{id}")
    public ResponseEntity<EventOutDto> modifyEvent(@PathVariable long id, @Valid @RequestBody EventInDto eventInDto)
            throws EventNotFoundException, SpeakerNotFoundException {

        EventOutDto updatedEvent = eventService.modify(id, eventInDto);
        return ResponseEntity.ok(updatedEvent);
    }

    // DELETE
    @DeleteMapping("/events/{id}")
    public ResponseEntity<Void> deleteEvent(@PathVariable long id) throws EventNotFoundException {
        eventService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 404 - Event
    @ExceptionHandler(EventNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(EventNotFoundException enfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The event does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 404 - Speaker (relación)
    @ExceptionHandler(SpeakerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(SpeakerNotFoundException snfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The speaker does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 400 - Validaciones
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleException(MethodArgumentNotValidException manve) {
        Map<String, String> errors = new HashMap<>();
        manve.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(fieldName, message);
        });

        ErrorResponse errorResponse = ErrorResponse.validationError(errors);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
