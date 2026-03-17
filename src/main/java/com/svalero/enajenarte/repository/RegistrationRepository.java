package com.svalero.enajenarte.repository;

import com.svalero.enajenarte.domain.Registration;
import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.domain.Workshop;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RegistrationRepository extends CrudRepository<Registration, Long> {

    List<Registration> findAll();

    // Filtros (3 campos)
    List<Registration> findByWorkshop(Workshop workshop);
    List<Registration> findByUser(User user);
    List<Registration> findByIsPaid(boolean isPaid);
}
