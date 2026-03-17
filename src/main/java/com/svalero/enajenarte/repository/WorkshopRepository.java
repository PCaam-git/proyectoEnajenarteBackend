package com.svalero.enajenarte.repository;

import com.svalero.enajenarte.domain.Speaker;
import com.svalero.enajenarte.domain.Workshop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface WorkshopRepository extends CrudRepository<Workshop, Long> {

    List<Workshop> findAll();

    List<Workshop> findBySpeaker(Speaker speaker);
    List<Workshop> findByNameContainingIgnoreCase(String name);
    List<Workshop> findByIsOnline(boolean isOnline);
}
