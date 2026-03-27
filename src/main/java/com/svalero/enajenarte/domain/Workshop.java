package com.svalero.enajenarte.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "Workshop")
@Table(name = "workshops")
public class Workshop {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotNull(message = "name is mandatory")
    private String name;

    @Column
    @NotNull(message = "description is mandatory")
    private String description;

    @Column(name = "start_date")
    @NotNull(message = "startDate is mandatory")
    @Future(message = "startDate must be in the future")
    private LocalDate startDate;

    @Column
    @NotNull(message = "Hour is mandatory")
    private String hour;

    @Column(name = "confirmation_deadline")
    @NotNull(message = "Confirmation deadline is mandatory")
    @Future(message = "The confirmation deadline must be in the future")
    private LocalDate confirmationDeadline;

    @Column(name = "duration_minutes")
    @Min(value = 1, message = "durationMinutes must be at least 1 minute")
    private int durationMinutes;

    @Column
    @Min(value = 0, message = "price must be a positive number")
    private float price;

    @Column(name = "minimum_participants")
    @Min(value = 1, message = "Minimum participants must be greater than or equal to 1")
    private Integer minimumParticipants;

    @Column(name = "max_capacity")
    @Min(value = 1, message = "maxCapacity must be at least 1")
    private int maxCapacity;

    @Column(name = "is_online")
    private boolean isOnline;

    @Column
    @NotNull
    private String status;

    @ManyToOne
    @JoinColumn(name = "speaker_id")
    private Speaker speaker;

    @OneToMany(mappedBy = "workshop")
    @JsonBackReference
    private List<Registration> registrations;
}
