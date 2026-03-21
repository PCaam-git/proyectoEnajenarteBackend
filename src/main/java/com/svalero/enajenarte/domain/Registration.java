package com.svalero.enajenarte.domain;

import com.svalero.enajenarte.domain.enums.PaymentStatus;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "Registration")
@Table(name = "registrations")
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "registration_date")
    @NotNull(message = "registration date is mandatory")
    private LocalDate registrationDate;

    @Column(name = "confirmation_code")
    @NotNull(message = "confirmationCode is mandatory")
    private String confirmationCode;

    @Column(name = "is_paid")
    private boolean isPaid;

    @Column(name = "number_of_tickets")
    @Min(value = 1, message = "must register at least 1 person")
    @Max(value = 5, message = "cannot register more than 5 people at once")
    private int numberOfTickets;

    @Column
    @Min(value = 0, message = "amountPaid must be positive")
    private float amountPaid;

    @Column
    @Min(value = 1, message = "rating must be between 1 and 5")
    @Max(value = 5, message = "rating must be between 1 and 5")
    private Integer rating;

    @Column
    @NotNull
    private String status;

    @Column(name = "payment_status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentStatus paymentStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "user is mandatory")
    private User user;

    @ManyToOne
    @JoinColumn(name = "workshop_id")
    @NotNull(message = "workshop is mandatory")
    private Workshop workshop;
}
