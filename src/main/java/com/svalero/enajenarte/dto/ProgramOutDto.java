package com.svalero.enajenarte.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProgramOutDto {

    private long id;
    private String name;
    private String description;
    private String location;
    private LocalDate initDate;
    private LocalDate finishDate;
    private String hour;
    private int durationMinutes;
    private LocalDate confirmationDeadline;
    private float price;
    private Integer minimumParticipants;
    private int maxCapacity;
    private boolean isOnline;
    private String status;

    private long speakerId;
    private String speakerName;
}