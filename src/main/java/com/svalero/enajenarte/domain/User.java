package com.svalero.enajenarte.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
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
@Entity(name = "User")
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column
    @NotNull(message = "username is mandatory")
    private String username;

    @Column
    @NotNull(message = "password is mandatory")
    private String password;

    @Column
    @NotNull(message = "email is mandatory")
    @Email(message = "email must be valid")
    private String email;

    @Column(name = "full_name")
    @NotNull(message = "fullName is mandatory")
    private String fullName;

    @Column
    @Min(value = 12, message = "age must be at least 12")
    @Max(value = 120, message = "age must be realistic")
    private int age;

    @Column(name = "is_active")
    private boolean active;

    @Column
    @Min(value = 0, message = "balance must be a positive number")
    private float balance;

    @Column
    private String role;

    @OneToMany(mappedBy = "user")
    @JsonBackReference
    private List<Registration> registrations;

}
