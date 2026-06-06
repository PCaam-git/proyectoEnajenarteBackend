package com.svalero.enajenarte.controller;

import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.dto.JwtResponseDto;
import com.svalero.enajenarte.dto.LoginRequestDto;
import com.svalero.enajenarte.exception.ErrorResponse;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.security.JwtUtils;
import com.svalero.enajenarte.service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtils jwtUtils;

    @PostMapping("/auth/login")
    public ResponseEntity<JwtResponseDto> login(@Valid @RequestBody LoginRequestDto loginRequestDto)
        throws UserNotFoundException {

        User user = userService.autenticate(
                loginRequestDto.getUsername(),
                loginRequestDto.getPassword()
        );

        String token = jwtUtils.generateJwtToken(user.getUsername());

        JwtResponseDto jwtResponseDto = new JwtResponseDto(
                token,
                user.getUsername(),
                user.getRole(),
                user.getId()
        );

        return ResponseEntity.ok(jwtResponseDto);
    }

    //401 - Credenciales incorrectas
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleException(UserNotFoundException unfe) {
        ErrorResponse errorResponse = ErrorResponse.generalError(401, "No autorizado", "Nombre de usuario o contraseña inválidos");
        return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
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
