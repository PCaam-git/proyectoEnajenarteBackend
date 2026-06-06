package com.svalero.enajenarte.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "AdminCalendar")
@Table(name = "admin_calendar")
public class AdminCalendar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotNull(message = "Debes indicar el título")
    private String title;

    @Column(name = "start_date")
    @NotNull(message = "Debes indicar la fecha de inicio")
    private LocalDate startDate;

    @Column(name = "end_date")
    @NotNull(message = "Debes indicar la fecha de finalización")
    private LocalDate endDate;

    @Column
    @NotNull(message = "Debes indicar la hora")
    private String hour;

    @Column(name = "duration_minutes")
    @Min(value = 1, message = "La duración debe ser, como mínimo, 1 minuto")
    private int durationMinutes;

    @Column
    @NotNull(message = "Debes indicar la categoría")
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "speaker_name")
    private String speakerName;
}

