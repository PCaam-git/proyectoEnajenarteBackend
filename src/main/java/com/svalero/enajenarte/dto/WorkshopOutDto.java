package com.svalero.enajenarte.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkshopOutDto {
    private long id;
    private String name;
    private String description;
    private LocalDate startDate;
    private int durationMinutes;
    private float price;
    private boolean isOnline;
    private long speakerId;
}
