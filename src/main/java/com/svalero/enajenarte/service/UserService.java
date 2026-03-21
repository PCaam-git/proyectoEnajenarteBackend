package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.domain.Registration;
import com.svalero.enajenarte.domain.enums.PaymentStatus;
import com.svalero.enajenarte.dto.UserInDto;
import com.svalero.enajenarte.dto.UserOutDto;
import com.svalero.enajenarte.dto.UserRegistrationOutDto;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.repository.UserRepository;
import com.svalero.enajenarte.repository.RegistrationRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.ArrayList;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RegistrationRepository registrationRepository;
    @Autowired
    private ModelMapper modelMapper;


    // GET all
    public List<UserOutDto> findAll(String username, String email, String active) {

        // Convertir parámetros a variables finales para el stream. Si el filtro no se usa, devuelve null. Si se usa, aplica el valor del filtro
        final String finalUsername = username.isEmpty() ? null : username.toLowerCase();
        final String finalEmail = email.isEmpty() ? null : email.toLowerCase();
        final Boolean finalActive = active.isEmpty() ? null : Boolean.parseBoolean(active);

        // filtrado con stream
        List<User> filteredusers = userRepository.findAll().stream()
                .filter(user -> finalUsername == null || user.getUsername().toLowerCase().contains(finalUsername))
                .filter(user -> finalEmail == null || user.getEmail().toLowerCase().contains(finalEmail))
                .filter(user -> finalActive == null || user.isActive() == finalActive)
                .toList();

        // Mapear DTOs
        List<UserOutDto> userOutDtoList =
                modelMapper.map(filteredusers, new TypeToken<List<UserOutDto>>() {
                }.getType());

        return userOutDtoList;
    }

    // GET by id
    public UserOutDto findById(long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);
        return modelMapper.map(user, UserOutDto.class);
    }

    public List<UserRegistrationOutDto> getUserRegistrations(long userId) throws UserNotFoundException {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        List<Registration> registrations = registrationRepository.findByUser(user);
        List<UserRegistrationOutDto> userRegistrationOutDtos = new ArrayList<>();

        for (Registration registration : registrations) {
            UserRegistrationOutDto userRegistrationOutDto = new UserRegistrationOutDto();

            userRegistrationOutDto.setRegistrationId(registration.getId());
            userRegistrationOutDto.setRegistrationDate(registration.getRegistrationDate());
            userRegistrationOutDto.setStatus(registration.getStatus());

            if (registration.getPaymentStatus() != null) {
                userRegistrationOutDto.setPaymentStatus(registration.getPaymentStatus().name());
            }

            userRegistrationOutDto.setWorkshopId(registration.getWorkshop().getId());
            userRegistrationOutDto.setWorkshopName(registration.getWorkshop().getName());
            userRegistrationOutDto.setWorkshopStartDate(registration.getWorkshop().getStartDate());
            userRegistrationOutDto.setWorkshopStatus(registration.getWorkshop().getStatus());

            userRegistrationOutDtos.add(userRegistrationOutDto);
        }
        return userRegistrationOutDtos;
    }

    // POST
    public UserOutDto add(UserInDto userInDto) {
        User user = modelMapper.map(userInDto, User.class);

        // generadas por el sistema
        user.setRole("USER");
        user.setActive(true);
        user.setBalance(0);

        User newUser = userRepository.save(user);
        return modelMapper.map(newUser, UserOutDto.class);
    }

    // PUT
    public UserOutDto modify(long id, UserInDto userInDto) throws UserNotFoundException {
        User existingUser = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        // Obtener usuario autenticado
        String authenticatedUsername = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        // Validación: solo el propio usuario o ADMIN
        if (!existingUser.getUsername().equals(authenticatedUsername)) {
            String role = org.springframework.security.core.context.SecurityContextHolder
                    .getContext()
                    .getAuthentication()
                    .getAuthorities()
                    .iterator()
                    .next()
                    .getAuthority();

            if (!role.equals("ROLE_ADMIN")) {
                throw new RuntimeException("You cannot modify another user");
            }
        }

        // Datos de sistema
        String role = existingUser.getRole();
        boolean active = existingUser.isActive();
        float balance = existingUser.getBalance();

        modelMapper.map(userInDto, existingUser);
        existingUser.setId(id);

        existingUser.setRole(role);
        existingUser.setActive(active);
        existingUser.setBalance(balance);

        User updateUser = userRepository.save(existingUser);
        return modelMapper.map(updateUser, UserOutDto.class);
    }

    // DELETE
    public void delete(long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        userRepository.delete(user);
    }

    // AUTENTICACIÓN
    public User autenticate(String username, String password) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UserNotFoundException();
        }

        if (!user.getPassword().equals(password)) {
            throw new UserNotFoundException();
        }
        return user;
    }
}
