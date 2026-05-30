package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.Program;
import com.svalero.enajenarte.domain.ProgramRegistration;
import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.domain.enums.PaymentStatus;
import com.svalero.enajenarte.dto.ProgramRegistrationInDto;
import com.svalero.enajenarte.dto.ProgramRegistrationOutDto;
import com.svalero.enajenarte.exception.*;
import com.svalero.enajenarte.repository.ProgramRegistrationRepository;
import com.svalero.enajenarte.repository.ProgramRepository;
import com.svalero.enajenarte.repository.UserRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ProgramRegistrationService {

    @Autowired
    private ProgramRegistrationRepository programRegistrationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ProgramRepository programRepository;
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private EmailService emailService;

    private static final String STATUS_CONFIRMED = "CONFIRMED";
    private static final String STATUS_PENDING = "PENDING";
    private static final PaymentStatus PAYMENT_STATUS_PENDING = PaymentStatus.PENDING;

    // POST
    public ProgramRegistrationOutDto add(ProgramRegistrationInDto programRegistrationInDto)
            throws UserNotFoundException, ProgramNotFoundException, DuplicateProgramRegistrationException, ProgramCapacityExceededException, AccessDeniedException {

        User user = userRepository.findById(programRegistrationInDto.getUserId())
                .orElseThrow(UserNotFoundException::new);

        validateRegistrationOwner(user);

        Program program = programRepository.findById(programRegistrationInDto.getProgramId())
                .orElseThrow(ProgramNotFoundException::new);

        boolean exists = programRegistrationRepository.existsByUserIdAndProgramId(
                programRegistrationInDto.getUserId(),
                programRegistrationInDto.getProgramId()
        );

        if (exists) {
            throw new DuplicateProgramRegistrationException();
        }

        List<ProgramRegistration> registrations = programRegistrationRepository.findByProgram(program);

        // número de plazas máximo
        int currentCapacity = registrations.stream()
                .mapToInt(ProgramRegistration::getNumberOfTickets)
                .sum();

        // número de participantes mínimo
        int currentParticipants = registrations.stream()
                .mapToInt(ProgramRegistration::getNumberOfTickets)
                .sum();

        int requestedTickets = programRegistrationInDto.getNumberOfTickets();

        if (currentCapacity + requestedTickets > program.getMaxCapacity()) {

            int availableSpots = program.getMaxCapacity() - currentCapacity;

            throw new ProgramCapacityExceededException(
                    "No hay suficientes plazas disponibles. Actualmente quedan " + availableSpots + " plazas"
            );
        }

        ProgramRegistration registration = buildRegistration(programRegistrationInDto, user, program);

        ProgramRegistration newRegistration = programRegistrationRepository.save(registration);

        try {
            simulateEmailConfirmation(newRegistration, currentParticipants + requestedTickets);
        } catch (Exception e) {
            System.err.println("No se ha podido enviar el email de confirmación en la inscripción del programa: " + e.getMessage());
        }

        ProgramRegistrationOutDto programRegistrationOutDto = modelMapper.map(newRegistration, ProgramRegistrationOutDto.class);
        programRegistrationOutDto.setFullName(newRegistration.getUser().getFullName());
        programRegistrationOutDto.setProgramName(newRegistration.getProgram().getName());
        programRegistrationOutDto.setPaymentStatus(newRegistration.getPaymentStatus());

        return programRegistrationOutDto;
    }

    // DELETE
    public void delete(long id) throws RegistrationNotFoundException {
        ProgramRegistration registration = programRegistrationRepository.findById(id)
                .orElseThrow(RegistrationNotFoundException::new);

        programRegistrationRepository.delete(registration);
    }

    // GET ALL
    public List<ProgramRegistrationOutDto> findAll(String programId, String userId, String isPaid) {

        final Long finalProgramId = programId.isEmpty() ? null : Long.parseLong(programId);
        final Long finalUserId = userId.isEmpty() ? null : Long.parseLong(userId);
        final Boolean finalIsPaid = isPaid.isEmpty() ? null : Boolean.parseBoolean(isPaid);

        List<ProgramRegistration> filtered = programRegistrationRepository.findAll().stream()
                .filter(r -> finalProgramId == null || r.getProgram().getId() == finalProgramId)
                .filter(r -> finalUserId == null || r.getUser().getId() == finalUserId)
                .filter(r -> finalIsPaid == null || r.isPaid() == finalIsPaid)
                .toList();

        List<ProgramRegistrationOutDto> outDtos =
                modelMapper.map(filtered, new TypeToken<List<ProgramRegistrationOutDto>>() {}.getType());

        for (int i = 0; i < filtered.size(); i++) {
            ProgramRegistration r = filtered.get(i);
            ProgramRegistrationOutDto dto = outDtos.get(i);

            if (r.getUser() != null) {
                dto.setFullName(r.getUser().getFullName());
            }
            if (r.getProgram() != null) {
                dto.setProgramName(r.getProgram().getName());
            }
            if (r.getPaymentStatus() != null) {
                dto.setPaymentStatus(r.getPaymentStatus());
            }
        }

        return outDtos;
    }

    // GET BY ID
    public ProgramRegistrationOutDto findById(long id) throws RegistrationNotFoundException {
        ProgramRegistration registration = programRegistrationRepository.findById(id)
                .orElseThrow(RegistrationNotFoundException::new);

        ProgramRegistrationOutDto dto = modelMapper.map(registration, ProgramRegistrationOutDto.class);
        dto.setFullName(registration.getUser().getFullName());
        dto.setProgramName(registration.getProgram().getName());
        dto.setPaymentStatus(registration.getPaymentStatus());

        return dto;
    }

    // PUT
    public ProgramRegistrationOutDto modify(long id, ProgramRegistrationInDto inDto)
            throws RegistrationNotFoundException, UserNotFoundException, ProgramNotFoundException, InvalidPaymentStatusException {

        ProgramRegistration existing = programRegistrationRepository.findById(id)
                .orElseThrow(RegistrationNotFoundException::new);

        User user = userRepository.findById(inDto.getUserId())
                .orElseThrow(UserNotFoundException::new);

        Program program = programRepository.findById(inDto.getProgramId())
                .orElseThrow(ProgramNotFoundException::new);

        LocalDateTime registrationDate = existing.getRegistrationDate();
        String confirmationCode = existing.getConfirmationCode();
        boolean paid = existing.isPaid();
        double amountPaid = existing.getAmountPaid();
        Integer rating = existing.getRating();
        String status = existing.getStatus();

        modelMapper.map(inDto, existing);
        existing.setId(id);

        existing.setUser(user);
        existing.setProgram(program);

        existing.setRegistrationDate(registrationDate);
        existing.setConfirmationCode(confirmationCode);
        existing.setPaid(paid);
        existing.setAmountPaid(amountPaid);
        existing.setRating(rating);
        existing.setStatus(status);

        if (inDto.getPaymentStatus() != null) {
            try {
                existing.setPaymentStatus(inDto.getPaymentStatus().toUpperCase());
            } catch (IllegalArgumentException e) {
                throw new InvalidPaymentStatusException();
            }
        }

        ProgramRegistration updated = programRegistrationRepository.save(existing);

        ProgramRegistrationOutDto dto = modelMapper.map(updated, ProgramRegistrationOutDto.class);
        dto.setFullName(updated.getUser().getFullName());
        dto.setProgramName(updated.getProgram().getName());
        dto.setPaymentStatus(updated.getPaymentStatus());

        return dto;
    }

    private ProgramRegistration buildRegistration(ProgramRegistrationInDto inDto, User user, Program program) {

        ProgramRegistration registration = modelMapper.map(inDto, ProgramRegistration.class);
        registration.setUser(user);
        registration.setProgram(program);

        registration.setRegistrationDate(LocalDateTime.now());
        registration.setConfirmationCode(UUID.randomUUID().toString());
        registration.setPaid(false);
        registration.setAmountPaid(0);
        registration.setRating(null);

        applyInitialStatus(registration, program);

        return registration;
    }

    private void applyInitialStatus(ProgramRegistration registration, Program program) {
        if (STATUS_CONFIRMED.equals(program.getStatus())) {
            registration.setStatus(STATUS_CONFIRMED);
        } else {
            registration.setStatus(STATUS_PENDING);
        }

        registration.setPaymentStatus(PAYMENT_STATUS_PENDING.name());
    }

    private void simulateEmailConfirmation(ProgramRegistration registration, int totalParticipants) {
        String subject = "Inscripción en " + registration.getProgram().getName();
        String text;

        if (registration.getProgram().getMinimumParticipants() != null
                && totalParticipants >= registration.getProgram().getMinimumParticipants()) {

            text = "Tu inscripción se ha realizado correctamente.\n\n"
                    + "Programa: " + registration.getProgram().getName() + "\n"
                    + "Código de confirmación: " + registration.getConfirmationCode();
        } else {
            text = "Tu inscripción se ha realizado correctamente.\n\n"
                    + "Programa: " + registration.getProgram().getName() + "\n"
                    + "Código de confirmación: " + registration.getConfirmationCode() + "\n\n"
                    + "Más adelante recibirás toda la información detallada del programa.";
        }

        emailService.sendEmail(
                registration.getUser().getEmail(),
                subject,
                text
        );
    }

    private void validateRegistrationOwner(User user) throws AccessDeniedException {
        String authenticatedUsername = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        boolean isAdmin = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        if (!isAdmin && !user.getUsername().equals(authenticatedUsername)) {
            throw new AccessDeniedException();
        }
    }
}