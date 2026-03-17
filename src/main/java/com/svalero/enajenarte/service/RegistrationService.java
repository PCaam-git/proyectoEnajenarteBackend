package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.Registration;
import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.domain.Workshop;
import com.svalero.enajenarte.dto.RegistrationInDto;
import com.svalero.enajenarte.dto.RegistrationOutDto;
import com.svalero.enajenarte.exception.RegistrationNotFoundException;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.exception.WorkshopNotFoundException;
import com.svalero.enajenarte.repository.RegistrationRepository;
import com.svalero.enajenarte.repository.UserRepository;
import com.svalero.enajenarte.repository.WorkshopRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cglib.core.Local;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class RegistrationService {

    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private WorkshopRepository workshopRepository;
    @Autowired
    private ModelMapper modelMapper;

    // POST
    public RegistrationOutDto add(RegistrationInDto registrationInDto) throws UserNotFoundException, WorkshopNotFoundException {
        User user = userRepository.findById(registrationInDto.getUserId())
                .orElseThrow(UserNotFoundException::new);

        Workshop workshop = workshopRepository.findById(registrationInDto.getWorkshopId())
                .orElseThrow(WorkshopNotFoundException::new);

        Registration registration = modelMapper.map(registrationInDto, Registration.class);
        registration.setUser(user);
        registration.setWorkshop(workshop);

        //Aquí se establecen los datos de sistema
        registration.setRegistrationDate(LocalDate.now());
        registration.setConfirmationCode(UUID.randomUUID().toString());
        registration.setPaid(false);
        registration.setAmountPaid(0);
        registration.setRating(null);

        Registration newRegistration = registrationRepository.save(registration);

        RegistrationOutDto registrationOutDto = modelMapper.map(newRegistration, RegistrationOutDto.class);
        registrationOutDto.setUserId(newRegistration.getUser().getId());
        registrationOutDto.setWorkshopId(newRegistration.getWorkshop().getId());

        return registrationOutDto;
    }

    // DELETE
    public void delete(long id) throws RegistrationNotFoundException {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(RegistrationNotFoundException::new);
        registrationRepository.delete(registration);
    }

    // GET ALL (Con filtros simultáneos)
    // He eliminado las excepciones para poder probar filtros sin recibir error 404
    public List<RegistrationOutDto> findAll(String workshopId, String userId, String isPaid) {

        // Variables finales para el stream. O utiliza el valor asignado en el filtro, o lo marca como null
        final Long finalWorkshopId = workshopId.isEmpty() ? null : Long.parseLong(workshopId);
        final Long finalUserId = userId.isEmpty() ? null : Long.parseLong(userId);
        final Boolean finalIsPaid = isPaid.isEmpty() ? null : Boolean.parseBoolean(isPaid);

        // Filtrado con stream. Después de filtrar, lo convierte en lista
        List<Registration> filteredRegistrations = registrationRepository.findAll().stream()
                .filter(registration -> finalWorkshopId == null || registration.getWorkshop().getId() == finalWorkshopId)
                .filter(registration -> finalUserId == null || registration.getUser().getId() == finalUserId)
                .filter(registration -> finalIsPaid == null || registration.isPaid() == finalIsPaid)
                .toList();

        // Mapear y setear IDs manualmente para evitar que User o Workshop salgan a 0
        List<RegistrationOutDto> registrationsOutDtos =
                modelMapper.map(filteredRegistrations, new TypeToken<List<RegistrationOutDto>>() {}.getType());

        for (int i = 0; i < filteredRegistrations.size(); i++) {
            Registration registration = filteredRegistrations.get(i);
            RegistrationOutDto registrationOutDto = registrationsOutDtos.get(i);

            if (registration.getUser() != null) {
                registrationOutDto.setUserId(registration.getUser().getId());
            }
            if (registration.getWorkshop() != null) {
                registrationOutDto.setWorkshopId(registration.getWorkshop().getId());
            }
        }

        return registrationsOutDtos;
    }


        // GET BY ID
    public RegistrationOutDto findById(long id) throws RegistrationNotFoundException {
        Registration registration = registrationRepository.findById(id)
                .orElseThrow(RegistrationNotFoundException::new);

        // Mapear -> Setear IDs -> Devolver. Evita que workshopId y userId salgan a 0
        RegistrationOutDto registrationOutDto = modelMapper.map(registration, RegistrationOutDto.class);
        registrationOutDto.setUserId(registration.getUser().getId());
        registrationOutDto.setWorkshopId(registration.getWorkshop().getId());

        return registrationOutDto;
    }

    // PUT
    public RegistrationOutDto modify(long id, RegistrationInDto registrationInDto) throws RegistrationNotFoundException, UserNotFoundException, WorkshopNotFoundException {
        Registration existingRegistration = registrationRepository.findById(id)
                .orElseThrow(RegistrationNotFoundException::new);

        User user = userRepository.findById(registrationInDto.getUserId())
                .orElseThrow(UserNotFoundException::new);

        Workshop workshop = workshopRepository.findById(registrationInDto.getWorkshopId())
                .orElseThrow(WorkshopNotFoundException::new);

        // Sistema. Estos datos NO se podrán modificar para evita que el usuario haga acciones malintencionadas.
            LocalDate registrationDate = existingRegistration.getRegistrationDate();
            String confirmationCode = existingRegistration.getConfirmationCode();
            boolean paid = existingRegistration.isPaid();
            float amountPaid = existingRegistration.getAmountPaid();
            Integer rating = existingRegistration.getRating();

            modelMapper.map(registrationInDto, existingRegistration);
            existingRegistration.setId(id);

            existingRegistration.setUser(user);
            existingRegistration.setWorkshop(workshop);

            existingRegistration.setRegistrationDate(registrationDate);
            existingRegistration.setConfirmationCode(confirmationCode);
            existingRegistration.setPaid(paid);
            existingRegistration.setAmountPaid(amountPaid);
            existingRegistration.setRating(rating);

            Registration updateRegistration = registrationRepository.save(existingRegistration);

        // Mapear -> Setear IDs -> Devolver. Evita que workshopId y userId salgan a 0
            RegistrationOutDto registrationOutDto = modelMapper.map(updateRegistration, RegistrationOutDto.class);
            registrationOutDto.setUserId(updateRegistration.getUser().getId());
            registrationOutDto.setWorkshopId(updateRegistration.getWorkshop().getId());

            return registrationOutDto;
        }

    }

