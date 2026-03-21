package com.svalero.enajenarte.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequestDto {

    @NotEmpty(message = "username is mandatory")
    private String username;

    @NotEmpty(message = "password is mandatory")
    private String password;
}
