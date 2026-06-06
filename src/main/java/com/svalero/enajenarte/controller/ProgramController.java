package com.svalero.enajenarte.controller;

import com.svalero.enajenarte.dto.ProgramInDto;
import com.svalero.enajenarte.dto.ProgramOutDto;
import com.svalero.enajenarte.exception.*;
import com.svalero.enajenarte.service.ProgramService;
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
public class ProgramController {

    @Autowired
    private ProgramService programService;

    // GET
    @GetMapping("/programs")
    public ResponseEntity<List<ProgramOutDto>> getAll(
            @RequestParam(value = "name", defaultValue = "") String name,
            @RequestParam(value = "location", defaultValue = "") String location,
            @RequestParam(value = "isOnline", defaultValue = "") String isOnline) {

        List<ProgramOutDto> programOutDto = programService.findAll(name, location, isOnline);
        if (programOutDto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(programOutDto);
    }

    // GET BY ID
    @GetMapping("/programs/{id}")
    public ResponseEntity<ProgramOutDto> get(@PathVariable long id) throws ProgramNotFoundException {
        ProgramOutDto programOutDto = programService.findById(id);
        return ResponseEntity.ok(programOutDto);
    }

    // POST
    @PostMapping("/programs")
    public ResponseEntity<ProgramOutDto> addProgram(@Valid @RequestBody ProgramInDto programInDto)
            throws SpeakerNotFoundException, InvalidDateRangeException, DuplicateProgramException {
        ProgramOutDto newProgram = programService.add(programInDto);
        return new ResponseEntity<>(newProgram, HttpStatus.CREATED);
    }

    // PUT
    @PutMapping("/programs/{id}")
    public ResponseEntity<ProgramOutDto> modifyProgram(@PathVariable long id, @Valid @RequestBody ProgramInDto programInDto)
            throws SpeakerNotFoundException, ProgramNotFoundException, InvalidDateRangeException, DuplicateProgramException {
        ProgramOutDto updateProgram = programService.modify(id, programInDto);
        return ResponseEntity.ok(updateProgram);
    }

    // DELETE
    @DeleteMapping("/programs/{id}")
    public ResponseEntity<Void> deleteProgram(@PathVariable long id) throws ProgramNotFoundException, HasAssociatedRegistrationsException {
        programService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 404 - Program
    @ExceptionHandler(ProgramNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(ProgramNotFoundException pnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El programa no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(HasAssociatedRegistrationsException.class)
    public ResponseEntity<ErrorResponse> handleException(HasAssociatedRegistrationsException hare) {
        ErrorResponse errorResponse = ErrorResponse.generalError(
                409,
                "conflict",
                hare.getMessage()
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // 404 - Speaker
    @ExceptionHandler(SpeakerNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(SpeakerNotFoundException snfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("El ponente no existe");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 409 - Programa duplicado
    @ExceptionHandler(DuplicateProgramException.class)
    public ResponseEntity<ErrorResponse> handleException(DuplicateProgramException dpe) {
        ErrorResponse errorResponse = ErrorResponse.generalError(
                409,
                "conflict",
                "No se puede guardar. Ya existe un programa con el mismo nombre, fecha y modalidad o ponente"
        );
        return new ResponseEntity<>(errorResponse, HttpStatus.CONFLICT);
    }

    // 400 - Fecha de confirmación posterior a la fecha de inicio
    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ErrorResponse> handleException(InvalidDateRangeException idre) {
        ErrorResponse errorResponse = ErrorResponse.generalError(400, "bad-request", idre.getMessage());
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