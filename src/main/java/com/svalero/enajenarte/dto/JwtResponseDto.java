package com.svalero.enajenarte.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class JwtResponseDto {

    private String token;
    private String username;
    private String role;
    private long id;
}
