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

    @NotEmpty(message = "firstName is mandatory")
    private String firstName;

    @NotEmpty(message = "lastName is mandatory")
    private String lastName;

    @NotEmpty(message = "email is mandatory")
    @Email(message = "email must be valid")
    private String email;

    @NotEmpty(message = "speciality is mandatory")
    private String speciality;

    @Min(value = 0, message = "years experience cannot be negative")
    private int yearsExperience;

    @Min(value = 0, message = "workshopHoursTotal must be positive")
    private float workshopHoursTotal;

    private boolean available;

    private LocalDate joinDate;
}
