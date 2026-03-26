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
public class ProgramInDto {

    @NotEmpty(message = "Name is mandatory")
    private String name;

    @NotEmpty(message = "Description is mandatory")
    private String description;

    @NotEmpty(message = "Location is mandatory")
    private String location;

    @NotNull(message = "Init date is mandatory")
    @Future(message = "The init date must be in the future")
    private LocalDate initDate;

    @NotNull(message = "Finish date is mandatory")
    @Future(message = "The finish date must be in the future")
    private LocalDate finishDate;

    @NotEmpty(message = "Hour is mandatory")
    private String hour;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int durationMinutes;

    @NotNull(message = "Confirmation deadline is mandatory")
    @Future(message = "The confirmation deadline must be in the future")
    private LocalDate confirmationDeadline;

    @Min(value = 1, message = "Minimum participants must be greater than or equal to 1")
    private Integer minimumParticipants;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int maxCapacity;

    private boolean isOnline;

    @Min(value = 1, message = "Speaker ID is mandatory")
    private long speakerId;
}