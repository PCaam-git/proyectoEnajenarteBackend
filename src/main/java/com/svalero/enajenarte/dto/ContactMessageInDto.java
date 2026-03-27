package com.svalero.enajenarte.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ContactMessageInDto {

    @NotEmpty(message = "fullName is mandatory")
    private String fullName;

    @NotEmpty(message = "email is mandatory")
    @Email(message = "email must be valid")
    private String email;

    @NotEmpty(message = "category is mandatory")
    private String category;

    @NotNull(message = "referenceId is mandatory")
    @Min(value = 1, message = "referenceId must be greater than 0")
    private Long referenceId;

    @NotEmpty(message = "message is mandatory")
    private String message;
}