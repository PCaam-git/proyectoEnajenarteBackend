package com.svalero.enajenarte.dto;

import com.svalero.enajenarte.domain.enums.AgeGroup;
import com.svalero.enajenarte.domain.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInDto {

    @NotEmpty(message = "Indica el nombre de usuario")
    private String username;

    @NotEmpty(message = "Indica la contraseña")
    private String password;

    @NotEmpty(message = "email is mandatory")
    @Email(message = "email must be valid")
    @Pattern(regexp = "^[\\x00-\\x7F]+$", message = "El email no puede incluir ñ, tildes, etc")
    private String email;

    @NotEmpty(message = "Indica el nombre completo")
    private String fullName;

    @NotNull (message = "Indica un teléfono")
    @Min(value = 600000000, message = "El número de teléfono debe tener 9 dígitos")
    @Max(value = 799999999, message = "El número de teléfono debe tener 9 dígitos")
    private int phone;

    @NotNull (message = "Selecciona una opción")
    private Gender gender;

    @NotNull (message = "Selecciona una opción")
    private AgeGroup ageGroup;


}
