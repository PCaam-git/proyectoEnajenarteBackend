package com.svalero.enajenarte.dto;

import com.svalero.enajenarte.domain.enums.AgeGroup;
import com.svalero.enajenarte.domain.enums.Gender;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.*;
import lombok.NoArgsConstructor;
import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserInDto {

    @NotEmpty(message = "username is mandatory")
    private String username;

    @NotEmpty(message = "password is mandatory")
    private String password;

    @NotEmpty(message = "email is mandatory")
    @Email(message = "email must be valid")
    private String email;

    @NotEmpty(message = "fullName is mandatory")
    private String fullName;

    @NotNull (message = "phone is mandatory")
    @Min(value = 600000000, message = "phone must have 9 digits")
    @Max(value = 799999999, message = "phone must have 9 digits")
    private int phone;

    @NotNull (message = "gender is mandatory")
    private Gender gender;

    @NotNull (message = "ageGroup is mandatory")
    private AgeGroup ageGroup;


}
