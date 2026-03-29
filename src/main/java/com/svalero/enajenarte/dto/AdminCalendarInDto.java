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

    @NotEmpty(message = "title is mandatory")
    private String title;

    @NotNull(message = "startDate is mandatory")
    private LocalDate startDate;

    @NotNull(message = "endDate is mandatory")
    private LocalDate endDate;

    @NotEmpty(message = "hour is mandatory")
    private String hour;

    @Min(value = 1, message = "durationMinutes must be at least 1 minute")
    private int durationMinutes;

    @NotEmpty(message = "category is mandatory")
    private String category;

    private String description;

    private String speakerName;
}