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

    @NotEmpty(message = "Name is mandatory")
    private String name;

    @NotEmpty(message = "Description is mandatory")
    private String description;

    @NotNull(message = "Date is mandatory")
    @Future(message = "The date must be in the future")
    private LocalDate startDate;

    @Min(value = 1, message = "Duration must be at least 1 minute")
    private int durationMinutes;

    @Min(value = 0, message = "Price must be 0 or positive")
    private float price;

    @Min(value = 1, message = "Capacity must be at least 1")
    private int maxCapacity;

    private boolean isOnline;

    @Min(value = 1, message = "Speaker ID is mandatory")
    private long speakerId;
}
