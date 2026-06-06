package com.svalero.enajenarte.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegistrationOutDto {
    private long id;
    private LocalDate registrationDate;
    private String confirmationCode;
    private boolean isPaid;
    private int numberOfTickets;
    private float amountPaid;
    private int rating;
    private String status;
    private String paymentStatus;

    private String username;
    private String workshopName;

    private long userId;
    private long workshopId;
}
