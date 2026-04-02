package com.svalero.enajenarte.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactMessageInDto {

    @NotEmpty(message = "Indica tu nombre completo, por favor")
    private String fullName;

    @NotEmpty(message = "Debes indicar un email de contacto")
    @Email(message = "El email debe ser válido")
    @Pattern(regexp = "^[\\x00-\\x7F]+$", message = "email must contain only ASCII characters")
    private String email;

    @NotEmpty(message = "Debes seleccionar una categoría")
    private String category;

    @NotNull(message = "Debes seleccionar un tema")
    @Min(value = 1, message = "referenceId must be greater than 0")
    private Long referenceId;

    @NotEmpty(message = "Añade tu mensaje, por favor")
    private String message;
}