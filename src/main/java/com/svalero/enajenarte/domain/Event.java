package com.svalero.enajenarte.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
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
@Entity(name = "Event")
@Table(name = "events")
public class Event {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotNull(message = "Debes rellenar el título")
    private String title;

    @Column
    @NotNull(message = "Debes rellenar la localización")
    private String location;

    @Column(name = "event_date")
    @NotNull(message = "Debes rellenar la fecha del evento")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime eventDate;

    @Column(name = "entry_fee")
    @Min(value = 0, message = "El precio de la entrada debe ser un número positivo")
    private float entryFee;

    @Column(name = "is_public")
    private boolean isPublic;

    @Column(name = "expected_attendance")
    @Min(value = 0, message = "La asistencia esperada debe ser un número positivo")
    private int expectedAttendance;

    @ManyToOne
    @JoinColumn(name = "speaker_id")
    private Speaker speaker;
}
