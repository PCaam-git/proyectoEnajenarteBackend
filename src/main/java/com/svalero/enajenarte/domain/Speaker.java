package com.svalero.enajenarte.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
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
@Entity(name = "Speaker")
@Table(name = "speakers")
public class Speaker {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(name = "first_name")
    @NotNull(message = "Debes indicar un nombre")
    private String firstName;

    @Column(name = "last_name")
    @NotNull(message = "Debes indicar un apellido")
    private String lastName;

    @Column
    @NotNull(message = "Debes indicar un email")
    @Email(message = "email must be valid")
    private String email;

    @Column
    @NotNull(message = "Debes indicar, al menos, una especialidad")
    private String speciality;

    @Column(name = "years_experience")
    @Min(value = 0, message = "Los años de experiencia deben ser un número positivo")
    private int yearsExperience;

    @Column(name = "workshop_hours_total")
    @Min(value = 0, message = "workshopHoursTotal must be positive")
    private float workshopHoursTotal;

    @Column(name = "is_available")
    private boolean available;

    @Column(name = "join_date")
    private LocalDate joinDate;

    @OneToMany(mappedBy = "speaker")
    @JsonBackReference
    private List<Workshop> workshops;
}
