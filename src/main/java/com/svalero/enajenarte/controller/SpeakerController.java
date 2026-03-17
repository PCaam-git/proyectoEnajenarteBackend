package com.svalero.enajenarte.controller;

import com.svalero.enajenarte.dto.SpeakerInDto;
import com.svalero.enajenarte.dto.SpeakerOutDto;
import com.svalero.enajenarte.exception.ErrorResponse;
import com.svalero.enajenarte.exception.SpeakerNotFoundException;
import com.svalero.enajenarte.exception.WorkshopNotFoundException;
import com.svalero.enajenarte.service.SpeakerService;
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
public class SpeakerController {

    @Autowired
    private SpeakerService speakerService;

    // GET
    @GetMapping("/speakers")
    public ResponseEntity<List<SpeakerOutDto>> getAll(
        @RequestParam(value = "speciality", defaultValue = "") String speciality,
        @RequestParam(value = "available", defaultValue = "") String available,
        @RequestParam(value = "yearsExperience", defaultValue = "") String yearsExperience) {

        List<SpeakerOutDto> speakerOutDto = speakerService.findAll(speciality, available, yearsExperience);
        // Si la lista está vacía, devuelve 204 No Content
        if( speakerOutDto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // Si hay resultados, devuelve 200 Ok con la lista
        return ResponseEntity.ok(speakerOutDto);
    }

    // GET BY ID
    @GetMapping("/speakers/{id}")
    public ResponseEntity<SpeakerOutDto> get(@PathVariable long id) throws SpeakerNotFoundException {
        SpeakerOutDto speakerOutDto = speakerService.findById(id);
        return ResponseEntity.ok(speakerOutDto);
    }

    // POST
    @PostMapping("/speakers")
    public ResponseEntity<SpeakerOutDto> addSpeaker(@Valid @RequestBody SpeakerInDto speakerInDto)
            throws WorkshopNotFoundException {
        SpeakerOutDto newSpeaker = speakerService.add(speakerInDto);
        return new ResponseEntity<>(newSpeaker, HttpStatus.CREATED);
    }

    // PUT
    @PutMapping("/speakers/{id}")
    public ResponseEntity<SpeakerOutDto> modifySpeaker(@PathVariable long id, @Valid @RequestBody SpeakerInDto speakerInDto)
        throws SpeakerNotFoundException, WorkshopNotFoundException {
        SpeakerOutDto updateSpeaker = speakerService.modify(id, speakerInDto);
        return ResponseEntity.ok(updateSpeaker);
    }

    // DELETE
    @DeleteMapping("/speakers/{id}")
    public ResponseEntity<Void> deleteSpeaker(@PathVariable long id) throws SpeakerNotFoundException {
        speakerService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 404 - Speaker
    @ExceptionHandler(SpeakerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(SpeakerNotFoundException snfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The speaker does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 404 - Workshop (relación)
    @ExceptionHandler(WorkshopNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(WorkshopNotFoundException wnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The workshop does not exist");
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