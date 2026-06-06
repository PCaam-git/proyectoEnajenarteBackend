package com.svalero.enajenarte.dto;

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
public class ProgramInDto {

    @NotEmpty(message = "Debes indicar un título")
    private String name;

    @NotEmpty(message = "Debes indicar una descripción")
    private String description;

    @NotEmpty(message = "Debes indicar una ubicación")
    private String location;

    @NotNull(message = "Debes indicar la fecha de inicio")
    private LocalDate initDate;

    @NotNull(message = "Debes indicar la fecha de finalización")
    private LocalDate finishDate;

    @NotEmpty(message = "Indica la hora de inicio")
    private String hour;

    @Min(value = 1, message = "La duración debe ser de, al menos, 1 minuto")
    private int durationMinutes;

    private LocalDate confirmationDeadline;

    @Min(value = 0, message = "El precio debe ser igual o mayor a 0")
    private float price;

    @Min(value = 1, message = "El número mínimo de participantes debe ser igual o superior a 1")
    private Integer minimumParticipants;

    @Min(value = 1, message = "El número máximo de participantes debe ser superior a 1")
    private int maxCapacity;

    private boolean isOnline;

    @NotEmpty(message = "Debes indicar el estado del programa")
    private String status;

    @Min(value = 1, message = "Speaker ID is mandatory")
    private long speakerId;
}