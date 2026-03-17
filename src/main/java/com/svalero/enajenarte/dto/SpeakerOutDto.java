package com.svalero.enajenarte.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpeakerOutDto {
    private long id;
    private String firstName;
    private String lastName;
    private String email;
    private String speciality;
    private int yearsExperience;
}
