package com.svalero.enajenarte.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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
    @NotNull(message = "Debes indicar un título")
    private String name;

    @Column(columnDefinition = "TEXT")
    @NotNull(message = "Debes indicar una descripción")
    private String description;

    @Column(name = "start_date")
    @NotNull(message = "Debes indicar una fecha de inicio")
    private LocalDate startDate;

    @Column
    @NotNull(message = "Debes indicar una hora de inicio")
    private String hour;

    @Column(name = "confirmation_deadline")
    private LocalDate confirmationDeadline;

    @Column(name = "duration_minutes")
    @Min(value = 1, message = "La duración debe ser al menos de 1 minuto")
    private int durationMinutes;

    @Column
    @Min(value = 0, message = "El precio debe ser positivo")
    private float price;

    @Column(name = "minimum_participants")
    @Min(value = 1, message = "El número mínimo de participantes debe ser igual o superior a 1")
    private Integer minimumParticipants;

    @Column(name = "max_capacity")
    @Min(value = 1, message = "La capacidad máxima debe ser al menos 1")
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
