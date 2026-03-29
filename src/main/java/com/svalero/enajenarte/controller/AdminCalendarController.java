package com.svalero.enajenarte.controller;

import com.svalero.enajenarte.dto.AdminCalendarInDto;
import com.svalero.enajenarte.dto.AdminCalendarOutDto;
import com.svalero.enajenarte.exception.AdminCalendarNotFoundException;
import com.svalero.enajenarte.exception.ErrorResponse;
import com.svalero.enajenarte.exception.InvalidDateRangeException;
import com.svalero.enajenarte.exception.InvalidStartDateTimeException;
import com.svalero.enajenarte.service.AdminCalendarService;
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
public class AdminCalendarController {

    @Autowired
    private AdminCalendarService adminCalendarService;

    // GET
    @GetMapping("/admin-calendar")
    public ResponseEntity<List<AdminCalendarOutDto>> getAll(
            @RequestParam(value = "category", defaultValue = "") String category,
            @RequestParam(value = "speakerName", defaultValue = "") String speakerName) {

        List<AdminCalendarOutDto> adminCalendarOutDtos = adminCalendarService.findAll(category, speakerName);

        if (adminCalendarOutDtos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }

        return ResponseEntity.ok(adminCalendarOutDtos);
    }

    // GET BY ID
    @GetMapping("/admin-calendar/{id}")
    public ResponseEntity<AdminCalendarOutDto> get(@PathVariable long id) throws AdminCalendarNotFoundException {
        AdminCalendarOutDto adminCalendarOutDto = adminCalendarService.findById(id);
        return ResponseEntity.ok(adminCalendarOutDto);
    }

    // POST
    @PostMapping("/admin-calendar")
    public ResponseEntity<AdminCalendarOutDto> add(@Valid @RequestBody AdminCalendarInDto adminCalendarInDto)
            throws InvalidDateRangeException, InvalidStartDateTimeException {

        AdminCalendarOutDto newAdminCalendar = adminCalendarService.add(adminCalendarInDto);
        return new ResponseEntity<>(newAdminCalendar, HttpStatus.CREATED);
    }

    // PUT
    @PutMapping("/admin-calendar/{id}")
    public ResponseEntity<AdminCalendarOutDto> modify(@PathVariable long id, @Valid @RequestBody AdminCalendarInDto adminCalendarInDto)
            throws AdminCalendarNotFoundException, InvalidDateRangeException, InvalidStartDateTimeException {

        AdminCalendarOutDto updatedAdminCalendar = adminCalendarService.modify(id, adminCalendarInDto);
        return ResponseEntity.ok(updatedAdminCalendar);
    }

    // DELETE
    @DeleteMapping("/admin-calendar/{id}")
    public ResponseEntity<Void> delete(@PathVariable long id) throws AdminCalendarNotFoundException {
        adminCalendarService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 404
    @ExceptionHandler(AdminCalendarNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(AdminCalendarNotFoundException acnfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The calendar block does not exist");
        return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
    }

    // 400 - EndDate after or equal StartDate
    @ExceptionHandler(InvalidDateRangeException.class)
    public ResponseEntity<ErrorResponse> handleException(InvalidDateRangeException idre) {
        ErrorResponse errorResponse = ErrorResponse.generalError(400, "bad-request", "endDate must be after or equal to startDate");
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    // 400 - StartDate future
    @ExceptionHandler(InvalidStartDateTimeException.class)
    public ResponseEntity<ErrorResponse> handleException(InvalidStartDateTimeException isdte) {
        ErrorResponse errorResponse = ErrorResponse.generalError(400, "bad-request", isdte.getMessage());
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