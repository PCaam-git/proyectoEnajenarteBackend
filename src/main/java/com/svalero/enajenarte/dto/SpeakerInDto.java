package com.svalero.enajenarte.dto;

import jakarta.persistence.Column;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SpeakerInDto {

    @NotEmpty(message = "Debes indicar el nombre")
    private String firstName;

    @NotEmpty(message = "Debes indicar el apellido")
    private String lastName;

    @NotEmpty(message = "Indica un email de contacto")
    @Email(message = "El email debe ser válido")
    @Pattern(regexp = "^[\\x00-\\x7F]+$", message = "email must contain only ASCII characters")
    private String email;

    @NotEmpty(message = "Indica, como mínimo, una especialidad")
    private String speciality;

    @Min(value = 0, message = "El número de años de experiencia debe ser superior a 0")
    private int yearsExperience;

    @Min(value = 0, message = "workshopHoursTotal must be positive")
    private float workshopHoursTotal;

    private boolean available;

    private LocalDate joinDate;
}
