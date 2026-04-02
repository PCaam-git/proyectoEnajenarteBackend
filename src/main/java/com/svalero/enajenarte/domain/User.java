package com.svalero.enajenarte.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.svalero.enajenarte.domain.enums.AgeGroup;
import com.svalero.enajenarte.domain.enums.Gender;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "User")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotNull(message = "Debes indicar un nombre de usuario")
    private String username;

    @Column
    @NotNull(message = "Debes indicar una contraseña")
    private String password;

    @Column
    @NotNull(message = "email is mandatory")
    @Email(message = "El email debe de ser válido")
    private String email;

    @Column(name = "full_name")
    @NotNull(message = "Debes indicar un nombre completo")
    private String fullName;

    @Column
    @NotNull(message = "phone is mandatory")
    @Min(value = 600000000, message = "El teléfono debe contener 9 cifras")
    @Max(value = 999999999, message = "El teléfono debe contener 9 cifras")
    private int phone;

    @Enumerated(EnumType.STRING)
    @Column
    @NotNull(message = "gender is mandatory")
    private Gender gender;

    @Enumerated(EnumType.STRING)
    @Column(name = "age_group")
    @NotNull(message = "Por favor, selecciona un grupo de edad")
    private AgeGroup ageGroup;

    @Column(name = "is_active")
    private boolean active;

    @Column
    private String role;

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private List<Registration> registrations;

}
