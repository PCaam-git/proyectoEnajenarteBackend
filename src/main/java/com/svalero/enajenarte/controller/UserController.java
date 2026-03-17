package com.svalero.enajenarte.controller;

import com.svalero.enajenarte.dto.UserInDto;
import com.svalero.enajenarte.dto.UserOutDto;
import com.svalero.enajenarte.exception.ErrorResponse;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.service.UserService;
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
public class UserController {

    @Autowired
    private UserService userService;

    // GET (con filtros: hasta 3 campos)
    @GetMapping("/users")
    public ResponseEntity<List<UserOutDto>> getAll(
            @RequestParam(value = "username", defaultValue = "") String username,
            @RequestParam(value = "email", defaultValue = "") String email,
            @RequestParam(value = "active", defaultValue = "") String active) {

        List<UserOutDto> usersOutDto = userService.findAll(username, email, active);
        // Si la lista está vacía, devuelve 204 No Content
        if( usersOutDto.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        // Si hay resultados, devuelve 200 Ok con la lista
        return ResponseEntity.ok(usersOutDto);
    }

    // GET by id
    @GetMapping("/users/{id}")
    public ResponseEntity<UserOutDto> get(@PathVariable long id) throws UserNotFoundException {
        UserOutDto userOutDto = userService.findById(id);
        return ResponseEntity.ok(userOutDto);
    }

    // POST
    @PostMapping("/users")
    public ResponseEntity<UserOutDto> addUser(@Valid @RequestBody UserInDto userInDto) {
        UserOutDto newUser = userService.add(userInDto);
        return new ResponseEntity<>(newUser, HttpStatus.CREATED);
    }

    // PUT
    @PutMapping("/users/{id}")
    public ResponseEntity<UserOutDto> modifyUser(@PathVariable long id, @Valid @RequestBody UserInDto userInDto)
            throws UserNotFoundException {
        UserOutDto updatedUser = userService.modify(id, userInDto);
        return ResponseEntity.ok(updatedUser);
    }

    // DELETE
    @DeleteMapping("/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable long id) throws UserNotFoundException {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // 404 - User
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserNotFoundException unfe) {
        ErrorResponse errorResponse = ErrorResponse.notFound("The user does not exist");
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
