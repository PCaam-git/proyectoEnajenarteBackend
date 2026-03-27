package com.svalero.enajenarte.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "ProgramRegistration")
@Table(name = "program_registrations")
public class ProgramRegistration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "registration_date")
    private LocalDateTime registrationDate;

    @Column(name = "confirmation_code")
    private String confirmationCode;

    @Column(name = "is_paid")
    private boolean isPaid;

    @Column(name = "amount_paid")
    @Min(value = 0, message = "amountPaid must be greater than or equal to 0")
    private double amountPaid;

    @Column(name = "number_of_tickets")
    private int numberOfTickets;

    @Column
    private Integer rating;

    @Column
    @NotNull
    private String status;

    @Column(name = "payment_status")
    @NotNull
    private String paymentStatus;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;
}