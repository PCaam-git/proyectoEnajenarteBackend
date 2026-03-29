package com.svalero.enajenarte.repository;

import com.svalero.enajenarte.domain.AdminCalendar;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface AdminCalendarRepository extends CrudRepository<AdminCalendar, Long> {

    List<AdminCalendar> findAll();

    List<AdminCalendar> findByCategory(String category);
    List<AdminCalendar> findBySpeakerNameContainingIgnoreCase(String speakerName);
}