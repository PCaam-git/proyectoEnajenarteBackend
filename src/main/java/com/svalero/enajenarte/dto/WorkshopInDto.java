package com.svalero.enajenarte.dto;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WorkshopInDto {

    @NotEmpty(message = "Debes indicar un título")
    private String name;

    @NotEmpty(message = "Debes indicar una descripción")
    private String description;

    @NotNull(message = "Debes indicar la fecha del taller")
    @Future(message = "La fecha debe ser futura")
    private LocalDate startDate;

    @NotEmpty(message = "Indica la hora del taller")
    private String hour;

    @NotNull(message = "Debes seleccionar una fecha de confirmación")
    @Future(message = "La fecha de confirmación debe ser futura")
    private LocalDate confirmationDeadline;

    @Min(value = 1, message = "La duración debe ser de, al menos, 1 minuto")
    private int durationMinutes;

    @Min(value = 0, message = "El precio debe ser positivo")
    private float price;

    @Min(value = 1, message = "El número mínimo de participantes debe ser igual o superior a 1")
    private Integer minimumParticipants;

    @Min(value = 1, message = "El número máximo de participantes debe ser igual o superior a 1")
    private int maxCapacity;

    private boolean isOnline;

    @Min(value = 1, message = "Speaker ID is mandatory")
    private long speakerId;
}
