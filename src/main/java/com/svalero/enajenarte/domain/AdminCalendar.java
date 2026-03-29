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
    @NotNull(message = "title is mandatory")
    private String title;

    @Column(name = "start_date")
    @NotNull(message = "startDate is mandatory")
    @Future(message = "startDate must be in the future")
    private LocalDate startDate;

    @Column(name = "end_date")
    @NotNull(message = "endDate is mandatory")
    @Future(message = "endDate must be in the future")
    private LocalDate endDate;

    @Column
    @NotNull(message = "hour is mandatory")
    private String hour;

    @Column(name = "duration_minutes")
    @Min(value = 1, message = "durationMinutes must be at least 1 minute")
    private int durationMinutes;

    @Column
    @NotNull(message = "category is mandatory")
    private String category;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "speaker_name")
    private String speakerName;
}

