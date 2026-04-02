package com.svalero.enajenarte.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotEmpty(message = "Introduce tu nombre de usuario")
    private String username;

    @NotEmpty(message = "Introduce tu contraseña")
    private String password;
}
