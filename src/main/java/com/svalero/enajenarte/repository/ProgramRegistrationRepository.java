package com.svalero.enajenarte.repository;

import com.svalero.enajenarte.domain.Program;
import com.svalero.enajenarte.domain.ProgramRegistration;
import com.svalero.enajenarte.domain.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ProgramRegistrationRepository extends CrudRepository<ProgramRegistration, Long> {

    boolean existsByUserIdAndProgramId(Long userId, Long programId);

    List<ProgramRegistration> findAll();

    // Filtros (3 campos)
    List<ProgramRegistration> findByProgram(Program program);
    List<ProgramRegistration> findByUser(User user);
    List<ProgramRegistration> findByIsPaid(boolean isPaid);
}