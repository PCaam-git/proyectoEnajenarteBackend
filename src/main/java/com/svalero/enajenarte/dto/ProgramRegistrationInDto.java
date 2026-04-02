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

    @Min(value = 1, message = "Debes reservar, al menos, 1 plaza")
    @Max(value = 5, message = "No es posible reservar más de 5 plazas en una inscripción")
    private int numberOfTickets;

    @Min(value = 1, message = "User ID must be greater than 0")
    private long userId;

    @Min(value = 1, message = "Program ID must be greater than 0")
    private long programId;

    private String paymentStatus;
}