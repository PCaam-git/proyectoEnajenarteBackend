package com.svalero.enajenarte.repository;

import com.svalero.enajenarte.domain.Program;
import com.svalero.enajenarte.domain.Speaker;
import org.springframework.data.repository.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProgramRepository extends CrudRepository<Program, Long> {

    List<Program> findAll();

    // Filtros (3 campos)
    List<Program> findByNameContainingIgnoreCase(String name);
    List<Program> findByLocationContainingIgnoreCase(String location);
    List<Program> findByIsOnline(boolean isOnline);

    List<Program> findBySpeaker(Speaker speaker);
    List<Program> findByInitDateAfter(LocalDate date);
}