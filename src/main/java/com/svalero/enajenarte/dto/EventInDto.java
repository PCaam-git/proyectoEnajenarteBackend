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

    @NotEmpty(message = "title is mandatory")
    private String title;

    @NotEmpty(message = "location is mandatory")
    private String location;

    @NotNull(message = "eventDate is mandatory")
    private LocalDateTime eventDate;

    @Min(value = 0, message = "entryFee must be a positive number")
    private float entryFee;

    private boolean isPublic;

    @Min(value = 0, message = "expectedAttendance must be a positive number")
    private int expectedAttendance;

    @Min(value = 1, message = "Speaker ID must be greater than 0")
    private long speakerId;
}
