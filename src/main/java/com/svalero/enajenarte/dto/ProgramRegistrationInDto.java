package com.svalero.enajenarte.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.validation.annotation.Validated;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Validated
public class ProgramRegistrationInDto {

    @Min(value = 1, message = "must register at least 1 person")
    @Max(value = 5, message = "cannot register more than 5 people at once")
    private int numberOfTickets;

    @Min(value = 1, message = "User ID must be greater than 0")
    private long userId;

    @Min(value = 1, message = "Program ID must be greater than 0")
    private long programId;

    private String paymentStatus;
}