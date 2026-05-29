package com.svalero.enajenarte.service;

import com.svalero.enajenarte.domain.User;
import com.svalero.enajenarte.domain.Registration;
import com.svalero.enajenarte.domain.enums.PaymentStatus;
import com.svalero.enajenarte.dto.UserEditInDto;
import com.svalero.enajenarte.dto.UserInDto;
import com.svalero.enajenarte.dto.UserOutDto;
import com.svalero.enajenarte.dto.UserRegistrationOutDto;
import com.svalero.enajenarte.exception.AccessDeniedException;
import com.svalero.enajenarte.exception.HasAssociatedRegistrationsException;
import com.svalero.enajenarte.exception.UserNotFoundException;
import com.svalero.enajenarte.repository.UserRepository;
import com.svalero.enajenarte.repository.RegistrationRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    @Autowired
    private PasswordEncoder passwordEncoder;


    // GET all
    public List<UserOutDto> findAll(String username, String email, String active) {

        // Convertir parámetros a variables finales para el stream. Si el filtro no se usa, devuelve null. Si se usa, aplica el valor del filtro
        final String finalUsername = username.isEmpty() ? null : username.toLowerCase();
        final String finalEmail = email.isEmpty() ? null : email.toLowerCase();
        final Boolean finalActive = active.isEmpty() ? null : Boolean.parseBoolean(active);

        // filtrado con stream
        List<User> filteredUsers = userRepository.findAll().stream()
                .filter(user -> finalUsername == null || user.getUsername().toLowerCase().contains(finalUsername))
                .filter(user -> finalEmail == null || user.getEmail().toLowerCase().contains(finalEmail))
                .filter(user -> finalActive == null || user.isActive() == finalActive)
                .toList();

        // Mapear DTOs
        List<UserOutDto> userOutDtoList =
                modelMapper.map(filteredUsers, new TypeToken<List<UserOutDto>>() {}.getType());

        for (int i = 0; i < filteredUsers.size(); i++) {
            User user = filteredUsers.get(i);
            UserOutDto dto = userOutDtoList.get(i);

            if (user.getGender() != null) {
                dto.setGender(user.getGender().getDisplayName());
            }
            if (user.getAgeGroup() != null) {
                dto.setAgeGroup(user.getAgeGroup().getDisplayName());
            }
        }

        return userOutDtoList;
    }

    // GET by id
    public UserOutDto findById(long id) throws UserNotFoundException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        UserOutDto userOutDto = modelMapper.map(user, UserOutDto.class);

        if (user.getGender() != null) {
            userOutDto.setGender(user.getGender().getDisplayName());
        }
        if (user.getAgeGroup() != null) {
            userOutDto.setAgeGroup(user.getAgeGroup().getDisplayName());
        }
        return userOutDto;
    }

    public List<UserRegistrationOutDto> getUserRegistrations(long userId) throws UserNotFoundException, AccessDeniedException {
        User user = userRepository.findById(userId)
                .orElseThrow(UserNotFoundException::new);

        // Usuario autenticado
        String authenticatedUsername = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        String authenticatedRole = org.springframework.security.core.context.SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getAuthorities()
                .iterator()
                .next()
                .getAuthority();

        // Solo el propio usuario o ADMIN
        if (!user.getUsername().equals(authenticatedUsername) && !authenticatedRole.equals("ROLE_ADMIN")) {
            throw new AccessDeniedException();
        }

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

        user.setPassword(passwordEncoder.encode(userInDto.getPassword()));

        // generadas por el sistema
        user.setRole("USER");
        user.setActive(true);

        User newUser = userRepository.save(user);

        UserOutDto userOutDto = modelMapper.map(newUser, UserOutDto.class);

        if (newUser.getGender() != null) {
            userOutDto.setGender(newUser.getGender().getDisplayName());
        }
        if (newUser.getAgeGroup() != null) {
            userOutDto.setAgeGroup(newUser.getAgeGroup().getDisplayName());
        }

        return userOutDto;
    }

    // PUT
    public UserOutDto modify(long id, UserEditInDto userEditInDto) throws UserNotFoundException {
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
                throw new RuntimeException("No puedes modificar los datos de otro usuario");
            }
        }

        existingUser.setEmail(userEditInDto.getEmail());
        existingUser.setFullName(userEditInDto.getFullName());
        existingUser.setPhone(userEditInDto.getPhone());
        existingUser.setGender(userEditInDto.getGender());
        existingUser.setAgeGroup(userEditInDto.getAgeGroup());

        if (userEditInDto.getPassword() != null
                && !userEditInDto.getPassword().isBlank()) {
            existingUser.setPassword(passwordEncoder.encode(userEditInDto.getPassword()));
        }

        User updatedUser = userRepository.save(existingUser);

        UserOutDto userOutDto = modelMapper.map(updatedUser, UserOutDto.class);

        if (updatedUser.getGender() != null) {
            userOutDto.setGender(updatedUser.getGender().getDisplayName());
        }
        if (updatedUser.getAgeGroup() != null) {
            userOutDto.setAgeGroup(updatedUser.getAgeGroup().getDisplayName());
        }

        return userOutDto;
    }

    // DELETE
    public void delete(long id) throws UserNotFoundException, HasAssociatedRegistrationsException {
        User user = userRepository.findById(id)
                .orElseThrow(UserNotFoundException::new);

        List<Registration> registrations = registrationRepository.findByUser(user);
        if (!registrations.isEmpty()) {
            throw new HasAssociatedRegistrationsException();
        }

        userRepository.delete(user);
    }

    // AUTENTICACIÓN
    public User autenticate(String username, String password) throws UserNotFoundException {
        User user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UserNotFoundException();
        }

        String storedPassword = user.getPassword();

        // comprueba si el usuario ya ha sido migrado a BCrypt
        if (storedPassword != null && storedPassword.startsWith("$2")) {
            if (!passwordEncoder.matches(password, storedPassword)) {
                throw new UserNotFoundException();
            }

            return user;
        }

        // Compatibilidad temporal con los usuarios existentes:
        if (storedPassword == null || !storedPassword.equals(password)) {
            throw new UserNotFoundException();
        }

        // Si el usuario antiguo se autentica correctamente, su contraseña se migra automáticamente a BCrypt
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);

        return user;
    }
}
