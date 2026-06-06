package com.svalero.enajenarte.dto;

import com.svalero.enajenarte.domain.enums.AgeGroup;
import com.svalero.enajenarte.domain.enums.Gender;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEditInDto {

    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String password;

    @NotEmpty(message = "Indica un email")
    @Email(message = "El email debe ser válido")
    @Pattern(regexp = "^[\\x00-\\x7F]+$", message = "El email no puede incluir ñ, tildes, etc")
    private String email;

    @NotEmpty(message = "Indica el nombre completo")
    private String fullName;

    @NotNull(message = "Indica un teléfono")
    @Min(value = 600000000, message = "El número de teléfono debe tener 9 dígitos")
    @Max(value = 799999999, message = "El número de teléfono debe tener 9 dígitos")
    private Integer phone;

    @NotNull(message = "Selecciona una opción")
    private Gender gender;

    @NotNull(message = "Selecciona una opción")
    private AgeGroup ageGroup;
}