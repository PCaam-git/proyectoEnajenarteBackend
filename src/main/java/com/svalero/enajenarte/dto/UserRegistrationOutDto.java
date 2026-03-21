package com.svalero.enajenarte.dto;

import com.svalero.enajenarte.domain.enums.PaymentStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.concurrent.atomic.LongAccumulator;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserRegistrationOutDto {
    private long registrationId;
    private LocalDate registrationDate;
    private String status;
    private String paymentStatus;

    private long workshopId;
    private String workshopName;
    private LocalDate workshopStartDate;
    private String workshopStatus;
}
