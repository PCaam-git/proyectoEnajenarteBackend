package com.svalero.enajenarte.controller;

import com.svalero.enajenarte.dto.ContactMessageInDto;
import com.svalero.enajenarte.dto.ContactMessageOutDto;
import com.svalero.enajenarte.exception.ContactMessageNotFoundException;
import com.svalero.enajenarte.exception.ErrorResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import com.svalero.enajenarte.service.ContactMessageService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class ContactMessageController {

    @Autowired
    private ContactMessageService contactMessageService;

    // GET
    @GetMapping("/contact-messages")
    public ResponseEntity<List<ContactMessageOutDto>> getAll(
            @RequestParam(value = "category", defaultValue = "") String category,
            @RequestParam(value = "email", defaultValue = "") String email) {

        List<ContactMessageOutDto> contactMessagesOutDto = contactMessageService.findAll(category, email);
        if (contactMessagesOutDto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(contactMessagesOutDto);
    }

    // GET by id
    @GetMapping("/contact-messages/{id}")
    public ResponseEntity<ContactMessageOutDto> get(@PathVariable long id) throws ContactMessageNotFoundException {
        ContactMessageOutDto contactMessageOutDto = contactMessageService.findById(id);
        return ResponseEntity.ok(contactMessageOutDto);
    }

    // POST
    @PostMapping("/contact-messages")
    public ResponseEntity<ContactMessageOutDto> add(@Valid @RequestBody ContactMessageInDto contactMessageInDto) {
        ContactMessageOutDto newContactMessage = contactMessageService.add(contactMessageInDto);
        return new ResponseEntity<>(newContactMessage, HttpStatus.CREATED);
    }

    // DELETE
    @DeleteMapping("/contact-messages/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws ContactMessageNotFoundException {
        contactMessageService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 404
    @ExceptionHandler(ContactMessageNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ContactMessageNotFoundException cmnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The contact message does not exist");
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