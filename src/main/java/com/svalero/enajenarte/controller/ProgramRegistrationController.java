package com.svalero.enajenarte.controller;

import com.svalero.enajenarte.dto.ProgramRegistrationInDto;
import com.svalero.enajenarte.dto.ProgramRegistrationOutDto;
import com.svalero.enajenarte.exception.*;
import com.svalero.enajenarte.service.ProgramRegistrationService;
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
public class ProgramRegistrationController {

    @Autowired
    private ProgramRegistrationService programRegistrationService;

    // GET (con filtros: hasta 3 campos)
    @GetMapping("/program-registrations")
    public ResponseEntity<List<ProgramRegistrationOutDto>> getAll(
            @RequestParam(value = "programId", defaultValue = "") String programId,
            @RequestParam(value = "userId", defaultValue = "") String userId,
            @RequestParam(value = "isPaid", defaultValue = "") String isPaid)
    {

        List<ProgramRegistrationOutDto> outDto = programRegistrationService.findAll(programId, userId, isPaid);

        if (outDto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(outDto);
    }

    // GET by id
    @GetMapping("/program-registrations/{id}")
    public ResponseEntity<ProgramRegistrationOutDto> get(@PathVariable long id) throws RegistrationNotFoundException {
        ProgramRegistrationOutDto outDto = programRegistrationService.findById(id);
        return ResponseEntity.ok(outDto);
    }

    // POST
    @PostMapping("/program-registrations")
    public ResponseEntity<ProgramRegistrationOutDto> add(@Valid @RequestBody ProgramRegistrationInDto inDto)
            throws UserNotFoundException, ProgramNotFoundException, DuplicateProgramRegistrationException, ProgramCapacityExceededException {

        ProgramRegistrationOutDto newRegistration = programRegistrationService.add(inDto);
        return new ResponseEntity<>(newRegistration, HttpStatus.CREATED);
    }

    // PUT
    @PutMapping("/program-registrations/{id}")
    public ResponseEntity<ProgramRegistrationOutDto> modify(@PathVariable long id, @Valid @RequestBody ProgramRegistrationInDto inDto)
            throws RegistrationNotFoundException, UserNotFoundException, ProgramNotFoundException, InvalidPaymentStatusException {

        ProgramRegistrationOutDto updated = programRegistrationService.modify(id, inDto);
        return ResponseEntity.ok(updated);
    }

    // DELETE
    @DeleteMapping("/program-registrations/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws RegistrationNotFoundException {
        programRegistrationService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 404 - Registration
    @ExceptionHandler(RegistrationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(RegistrationNotFoundException rnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("La inscripción al programa no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 404 - User
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserNotFoundException unfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El usuario no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 404 - Program
    @ExceptionHandler(ProgramNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ProgramNotFoundException pnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El programa no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 400 - Inscripción duplicada
    @ExceptionHandler(DuplicateProgramRegistrationException.class)
    public ResponseEntity<ErrorResponse> handleException(DuplicateProgramRegistrationException dpre) {
        ErrorResponse errorResponse = ErrorResponse.generalError(400, "bad-request", dpre.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 400 - Máximo de participantes excedido
    @ExceptionHandler(ProgramCapacityExceededException.class)
    public ResponseEntity<ErrorResponse> handleException(ProgramCapacityExceededException pcee) {
        ErrorResponse errorResponse = ErrorResponse.generalError(400, "bad-request", pcee.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 400 - Estado de pago incorrecto
    @ExceptionHandler(InvalidPaymentStatusException.class)
    public ResponseEntity<ErrorResponse> handleException(InvalidPaymentStatusException ipse) {
        ErrorResponse errorResponse = ErrorResponse.generalError(400, "bad-request", ipse.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
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