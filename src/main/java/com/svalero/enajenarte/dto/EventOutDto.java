package com.svalero.enajenarte.dto;

import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EventOutDto {
    private long id;
    private String title;
    private String location;
    private LocalDateTime eventDate;
    private float entryFee;
    private boolean isPublic;

    private long speakerId;
}
