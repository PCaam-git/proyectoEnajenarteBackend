package com.svalero.enajenarte.repository;

import com.svalero.enajenarte.domain.Event;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EventRepository extends CrudRepository<Event, Long> {

    List<Event> findAll();

    // Filtros (3 campos)
    List<Event> findByTitleContainingIgnoreCase(String title);
    List<Event> findByLocationContainingIgnoreCase(String location);
    List<Event> findByIsPublic(boolean isPublic);
}
