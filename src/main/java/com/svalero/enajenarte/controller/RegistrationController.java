package com.svalero.enajenarte.controller;

import com.svalero.enajenarte.dto.RegistrationInDto;
import com.svalero.enajenarte.dto.RegistrationOutDto;
import com.svalero.enajenarte.exception.ErrorResponse;
import com.svalero.enajenarte.exception.RegistrationNotFoundException;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.exception.WorkshopNotFoundException;
import com.svalero.enajenarte.service.RegistrationService;
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
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    // GET (con filtros: hasta 3 campos)
    @GetMapping("/registrations")
    public ResponseEntity<List<RegistrationOutDto>> getAll(
            @RequestParam(value = "userId", defaultValue = "") String userId,
            @RequestParam(value = "workshopId", defaultValue = "") String workshopId,
            @RequestParam(value = "isPaid", defaultValue = "") String isPaid)
    {

        List<RegistrationOutDto> registrationsOutDto = registrationService.findAll(userId, workshopId, isPaid);

        // Si la lista está vacía, devuelve 204 No content
        if (registrationsOutDto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // Si hay resultados, devuelve 200 Ok con la lista
        return ResponseEntity.ok(registrationsOutDto);
    }

    // GET by id
    @GetMapping("/registrations/{id}")
    public ResponseEntity<RegistrationOutDto> get(@PathVariable long id) throws RegistrationNotFoundException {
        RegistrationOutDto registrationOutDto = registrationService.findById(id);
        return ResponseEntity.ok(registrationOutDto);
    }

    // POST
    @PostMapping("/registrations")
    public ResponseEntity<RegistrationOutDto> addRegistration(@Valid @RequestBody RegistrationInDto registrationInDto)
            throws UserNotFoundException, WorkshopNotFoundException {

        RegistrationOutDto newRegistration = registrationService.add(registrationInDto);
        return new ResponseEntity<>(newRegistration, HttpStatus.CREATED);
    }

    // PUT
    @PutMapping("/registrations/{id}")
    public ResponseEntity<RegistrationOutDto> modifyRegistration(@PathVariable long id, @Valid @RequestBody RegistrationInDto registrationInDto)
            throws RegistrationNotFoundException, UserNotFoundException, WorkshopNotFoundException {

        RegistrationOutDto updatedRegistration = registrationService.modify(id, registrationInDto);
        return ResponseEntity.ok(updatedRegistration);
    }

    // DELETE
    @DeleteMapping("/registrations/{id}")
    public ResponseEntity<Void> deleteRegistration(@PathVariable long id) throws RegistrationNotFoundException {
        registrationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 404 - Registration
    @ExceptionHandler(RegistrationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(RegistrationNotFoundException rnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The registration does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 404 - User (relación)
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserNotFoundException unfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The user does not exist");
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
