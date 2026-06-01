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
@Entity(name = "Program")
@Table(name = "programs")
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotNull(message = "Debes indicar un título")
    private String name;

    @Column(columnDefinition = "TEXT")
    @NotNull(message = "Debes incluir una descripción")
    private String description;

    @Column
    @NotNull(message = "Debes indicar una ubicación")
    private String location;

    @Column(name = "init_date")
    @NotNull(message = "Debes indicar una fecha de inicio")
    private LocalDate initDate;

    @Column(name = "finish_date")
    @NotNull(message = "Debes indicar una fecha de finalización")
    private LocalDate finishDate;

    @Column
    @NotNull(message = "Debes indicar una hora de comienzo")
    private String hour;

    @Column(name = "duration_minutes")
    @Min(value = 1, message = "La duración debe ser de, al menos, 1 minuto")
    private int durationMinutes;

    @Column(name = "confirmation_deadline")
    private LocalDate confirmationDeadline;

    @Column
    @Min(value = 0, message = "El precio debe ser positivo")
    private float price;

    @Column(name = "minimum_participants")
    @Min(value = 1, message = "El número mínimo de participantes es 1")
    private Integer minimumParticipants;

    @Column(name = "max_capacity")
    @Min(value = 1, message = "La capacidad máxima debe ser superior a 1")
    private int maxCapacity;

    @Column(name = "is_online")
    private boolean isOnline;

    @Column
    @NotNull
    private String status;

    @ManyToOne
    @JoinColumn(name = "speaker_id")
    private Speaker speaker;

    @OneToMany(mappedBy = "program")
    @JsonBackReference
    private List<ProgramRegistration> registrations;
}