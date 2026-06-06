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
@AllArgsConstructor
@NoArgsConstructor
public class AdminCalendarInDto {

    @NotEmpty(message = "Debes indicar un título")
    private String title;

    @NotNull(message = "Debes indicar una fecha de inicio")
    private LocalDate startDate;

    @NotNull(message = "Debes indicar una fecha de finalización")
    private LocalDate endDate;

    @NotEmpty(message = "Debes indicar una hora de inicio")
    private String hour;

    @Min(value = 1, message = "La duración debe ser de, al menos, 1 minuto")
    private int durationMinutes;

    @NotEmpty(message = "Debes seleccionar una categoría")
    private String category;

    private String description;

    private String speakerName;
}