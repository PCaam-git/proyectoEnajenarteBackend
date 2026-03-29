package com.svalero.enajenarte.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AdminCalendarOutDto {

    private long id;
    private String title;
    private LocalDate startDate;
    private LocalDate endDate;
    private String hour;
    private int durationMinutes;
    private String category;
    private String description;
    private String speakerName;
}