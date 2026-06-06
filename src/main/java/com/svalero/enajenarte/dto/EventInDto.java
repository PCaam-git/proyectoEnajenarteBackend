package com.svalero.enajenarte.dto;

import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventInDto {

    @NotEmpty(message = "El título es obligatorio")
    private String title;

    @NotEmpty(message = "Debes indicar una ubicación")
    private String location;

    @NotNull(message = "Debes indicar la fecha del evento")
    private LocalDateTime eventDate;

    @Min(value = 0, message = "El precio de la entrada debe ser 0 o superior")
    private float entryFee;

    private boolean isPublic;

    @Min(value = 0, message = "La asistencia esperada debe ser un número positivo")
    private int expectedAttendance;

    @Min(value = 1, message = "Speaker ID must be greater than 0")
    private long speakerId;
}
