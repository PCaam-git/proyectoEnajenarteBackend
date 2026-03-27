package com.svalero.enajenarte.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramRegistrationOutDto {
    private long id;
    private LocalDateTime registrationDate;
    private String confirmationCode;
    private boolean isPaid;
    private int numberOfTickets;
    private float amountPaid;
    private int rating;
    private String status;
    private String paymentStatus;

    private String fullName;
    private String programName;

}