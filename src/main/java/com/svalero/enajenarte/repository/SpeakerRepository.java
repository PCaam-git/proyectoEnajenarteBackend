package com.svalero.enajenarte.repository;

import com.svalero.enajenarte.domain.Speaker;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SpeakerRepository extends CrudRepository<Speaker, Long> {

    List<Speaker> findAll();

    // Filtros (3 campos)
    List<Speaker> findBySpecialityContainingIgnoreCase(String speciality);
    List<Speaker> findByAvailable(boolean available);
    List<Speaker> findByYearsExperience(int yearsExperience);
}
